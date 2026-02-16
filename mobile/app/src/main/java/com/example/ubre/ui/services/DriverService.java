package com.example.ubre.ui.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.example.ubre.ui.apis.ApiClient;
import com.example.ubre.ui.apis.DriverApi;
import com.example.ubre.ui.dtos.DriverRegistrationDto;
import com.example.ubre.ui.dtos.UserDto;
import com.example.ubre.ui.utils.TopToast;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverService {
    private static DriverService instance;
    private final Context context;

    private DriverService(Context context) {
        this.context = context.getApplicationContext();
    }

    public static DriverService getInstance(Context context) {
        if (instance == null) {
            instance = new DriverService(context);
        }
        return instance;
    }

    public void registerDriver(DriverRegistrationDto dto, Uri avatarUri, Listener listener) {
        DriverApi api = ApiClient.getClient().create(DriverApi.class);
        SharedPreferences sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt", null);

        if (token == null) {
            TopToast.show(context, "Registration failed", "Unauthorized. Please login again.");
            if (listener != null) listener.onFailure();
            return;
        }

        api.registerDriver("Bearer " + token, dto).enqueue(new Callback<UserDto>() {
            @Override
            public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Long driverId = response.body().getId();
                    if (driverId == null) {
                        TopToast.show(context, "Registration failed",
                                "Registration couldn't be completed. Missing driver id (Error 500).");
                        if (listener != null) listener.onFailure();
                        return;
                    }
                    if (avatarUri != null) {
                        uploadAvatar(api, token, driverId, avatarUri, listener);
                    } else {
                        if (listener != null) listener.onSuccess(response.body());
                    }
                    return;
                }

                String message = buildErrorMessage(response, null);
                TopToast.show(context, "Registration failed", message);
                if (listener != null) listener.onFailure();
            }

            @Override
            public void onFailure(Call<UserDto> call, Throwable t) {
                String message = buildErrorMessage(null, t);
                TopToast.show(context, "Registration failed", message);
                if (listener != null) listener.onFailure();
            }
        });
    }

    private void uploadAvatar(DriverApi api, String token, Long driverId, Uri avatarUri, Listener listener) {
        byte[] avatarBytes;
        try (InputStream is = context.getContentResolver().openInputStream(avatarUri)) {
            if (is == null) throw new IOException("Cannot open image");
            avatarBytes = readAllBytes(is);
        } catch (Exception e) {
            String message = "Registration couldn't be completed. " + e.getMessage() + " (Error 0).";
            TopToast.show(context, "Registration failed", message);
            if (listener != null) listener.onFailure();
            return;
        }

        RequestBody rb = RequestBody.create(avatarBytes, MediaType.parse("image/*"));
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", "avatar.jpg", rb);

        api.uploadDriverAvatar("Bearer " + token, driverId, part).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    if (listener != null) listener.onSuccess(null);
                } else {
                    String message = buildErrorMessage(response, null);
                    TopToast.show(context, "Registration failed", message);
                    if (listener != null) listener.onFailure();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                String message = buildErrorMessage(null, t);
                TopToast.show(context, "Registration failed", message);
                if (listener != null) listener.onFailure();
            }
        });
    }

    private String buildErrorMessage(Response<?> response, Throwable t) {
        if (response != null) {
            int code = response.code();
            if (code == 401) return "Unauthorized. Please login again.";
            if (code == 403) return "Forbidden. You are not authorized to access this resource.";

            String body = readErrorBody(response.errorBody());
            if (body != null && !body.trim().isEmpty()) {
                String detail = tryGetDetail(body);
                if (detail != null) return detail;
                if (!looksLikeJson(body)) return body.trim();
            }

            String reason = body != null && !body.trim().isEmpty() ? body.trim() : response.message();
            return "Registration couldn't be completed. " + reason + " (Error " + code + ").";
        }

        String reason = (t != null && t.getMessage() != null) ? t.getMessage() : "Unknown error";
        return "Registration couldn't be completed. " + reason + " (Error 0).";
    }

    private String readErrorBody(ResponseBody errorBody) {
        if (errorBody == null) return null;
        try {
            return errorBody.string();
        } catch (Exception e) {
            return null;
        }
    }

    private String tryGetDetail(String body) {
        try {
            JSONObject obj = new JSONObject(body);
            if (obj.has("detail")) return obj.getString("detail");
        } catch (Exception ignored) {
        }
        return null;
    }

    private boolean looksLikeJson(String body) {
        String s = body.trim();
        return s.startsWith("{") || s.startsWith("[");
    }

    private byte[] readAllBytes(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int n;
        while ((n = is.read(data)) != -1) buffer.write(data, 0, n);
        return buffer.toByteArray();
    }

    public interface Listener {
        void onSuccess(UserDto user);
        void onFailure();
    }
}
