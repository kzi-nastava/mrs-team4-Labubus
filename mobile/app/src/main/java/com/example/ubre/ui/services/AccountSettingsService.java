package com.example.ubre.ui.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.ubre.ui.apis.AccountSettingsApi;
import com.example.ubre.ui.apis.ApiClient;
import com.example.ubre.ui.dtos.UserDto;
import com.example.ubre.ui.storages.UserStorage;

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
}
