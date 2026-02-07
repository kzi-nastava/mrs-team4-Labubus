package com.example.ubre.ui.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.Toast;

import com.example.ubre.ui.apis.AccountSettingsApi;
import com.example.ubre.ui.apis.ApiClient;
import com.example.ubre.ui.dtos.UserDto;
import com.example.ubre.ui.storages.UserStorage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountSettingsService {
    private static final String TAG = "AccountSettingsService";
    private static AccountSettingsService instance;
    private Context context;

    private AccountSettingsService(Context context) {
        this.context = context.getApplicationContext();
    }

    public static AccountSettingsService getInstance(Context context) {
        if (instance == null) {
            instance = new AccountSettingsService(context);
        }
        return instance;
    }

    // save profile changes
    public void saveProfileChanges() throws Exception {
        AccountSettingsApi api = ApiClient.getClient().create(AccountSettingsApi.class);
        SharedPreferences sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt", null);

        if (token == null) {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show();
            throw new Exception("User not authenticated");
        }

        String userIdString = sharedPreferences.getString("id", null);
        Long userId = userIdString != null ? Long.parseLong(userIdString) : null;
        UserDto body = UserStorage.getInstance().getCurrentUser().getValue();
        if (body == null) { throw new Exception("No user data to update"); }

        api.updateUserProfile("Bearer " + token, userId, body).enqueue(new Callback<UserDto>() {
            @Override
            public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<UserDto> call, Throwable t) {
                Toast.makeText(context, "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // update user avater
    public void updateUserAvatar() throws Exception {
        AccountSettingsApi api = ApiClient.getClient().create(AccountSettingsApi.class);
        SharedPreferences sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt", null);

        if (token == null) {
            throw new Exception("User not authenticated");
        }

        String userIdString = sharedPreferences.getString("id", null);
        Long userId = userIdString != null ? Long.parseLong(userIdString) : null;

        byte[] avatarBytes;
        Uri pendingUri = UserStorage.getInstance().getPendingAvatarUri().getValue();
        if (pendingUri == null) {
            throw new Exception("No pending avatar to upload");
        }

        try (InputStream is = context.getContentResolver().openInputStream(pendingUri)) {
            if (is == null) throw new Exception("Cannot open image");
            avatarBytes = readAllBytes(is);
        }

        RequestBody rb  = RequestBody.create(avatarBytes, MediaType.parse("image/*"));
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", "avatar.jpg", rb);

        api.updateUserAvatar("Bearer " + token, userId, part).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // update user avater in storage
                    UserStorage.getInstance().setCurrentUserAvatar(avatarBytes);
                    // FIXME: after this action, avater url in current user dto is not updated, but it should not be a problem...
                    // FIXME: after every fetch of current user, fresh avatar will be fetched, so it should be ok, but it is not optimal solution:
                } else {
                    Toast.makeText(context, "Failed to update avatar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Failed to update avatar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private byte[] readAllBytes(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int n;
        while ((n = is.read(data)) != -1) buffer.write(data, 0, n);
        return buffer.toByteArray();
    }
}
