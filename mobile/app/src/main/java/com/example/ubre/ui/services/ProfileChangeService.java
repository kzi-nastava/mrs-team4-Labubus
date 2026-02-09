package com.example.ubre.ui.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.ubre.ui.apis.AccountSettingsApi;
import com.example.ubre.ui.apis.ApiClient;
import com.example.ubre.ui.apis.ProfileChangeApi;
import com.example.ubre.ui.dtos.ProfileChangeDto;
import com.example.ubre.ui.dtos.UserDto;
import com.example.ubre.ui.storages.ProfileChangeStorage;
import com.example.ubre.ui.storages.UserStorage;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileChangeService {
    private static final String TAG = "ProfileChangeService";
    private static ProfileChangeService instance;
    private Context context;

    private ProfileChangeService(Context context) {
        this.context = context.getApplicationContext();
    }

    public static ProfileChangeService getInstance(Context context) {
        if (instance == null) {
            instance = new ProfileChangeService(context);
        }
        return instance;
    }

    // load pending profile changes
    public void loadPendingProfileChanges() throws Exception {
        ProfileChangeApi api = ApiClient.getClient().create(ProfileChangeApi.class);
        SharedPreferences sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt", null);

        if (token == null) {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show();
            throw new Exception("User not authenticated");
        }

        // list to fill is in ProfileChangeStorage
        api.getPendingProfileChanges("Bearer " + token).enqueue(new Callback<List<ProfileChangeDto>>() {
            @Override
            public void onResponse(Call<List<ProfileChangeDto>> call, Response<List<ProfileChangeDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ProfileChangeDto> changes = response.body();
                    ProfileChangeStorage.getInstance().setProfileChanges(changes);
                } else {
                    Toast.makeText(context, "Failed to load pending profile changes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ProfileChangeDto>> call, Throwable t) {
                Toast.makeText(context, "Error loading pending profile changes: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // approve
    public void approve(ProfileChangeDto item) throws Exception {
        ProfileChangeApi api = ApiClient.getClient().create(ProfileChangeApi.class);
        SharedPreferences sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt", null);

        if (token == null) {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show();
            throw new Exception("User not authenticated");
        }

        api.approveProfileChange("Bearer " + token, item.getId()).enqueue(new Callback<Void>() {
            @Override public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    ProfileChangeStorage.getInstance().postRemove(item);
                    Toast.makeText(context, "Approved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Approve failed", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Approve error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // reject
    public void reject(ProfileChangeDto item) throws Exception {
        ProfileChangeApi api = ApiClient.getClient().create(ProfileChangeApi.class);
        SharedPreferences sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt", null);

        if (token == null) {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show();
            throw new Exception("User not authenticated");
        }

        api.rejectProfileChange("Bearer " + token, item.getId()).enqueue(new Callback<Void>() {
            @Override public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    ProfileChangeStorage.getInstance().postRemove(item);
                    Toast.makeText(context, "Rejected", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Reject failed", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Reject error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // fetch drivers avatar
    public void fetchDriverAvatar(Long driverId) throws Exception {
        ProfileChangeApi api = ApiClient.getClient().create(ProfileChangeApi.class);
        SharedPreferences sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt", null);

        if (token == null) {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show();
            throw new Exception("User not authenticated");
        }

        api.getDriverAvatar("Bearer " + token, driverId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        byte[] avatarBytes = response.body().bytes();
                        ProfileChangeStorage.getInstance().putAvatar(driverId, avatarBytes);
                    } catch (Exception e) {
                        Toast.makeText(context, "Error processing avatar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Failed to load driver avatar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "Error loading driver avatar: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
