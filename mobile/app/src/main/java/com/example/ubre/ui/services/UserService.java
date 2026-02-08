package com.example.ubre.ui.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.ubre.ui.apis.ApiClient;
import com.example.ubre.ui.apis.UserApi;
import com.example.ubre.ui.dtos.UserDto;
import com.example.ubre.ui.main.MainActivity;
import com.example.ubre.ui.storages.RideHistoryStorage;
import com.example.ubre.ui.storages.UserStorage;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserService {
    private static final String TAG = "UserService";
    private static UserService instance;
    private Context context;

    private UserService(Context context) {
        this.context = context.getApplicationContext();
    }

    public static UserService getInstance(Context context) {
        if (instance == null) {
            instance = new UserService(context);
        }
        return instance;
    }

    public void getUserById() throws Exception {
//        UserApi userApi = ApiClient.getClient().create(UserApi.class);
//        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
//        String token = sharedPreferences.getString("jwt", null);
//
//        if (token == null) {
//            throw new Exception("User not authenticated");
//        }
//
//        userApi.getUserById("Bearer " + token, ).enqueue(new Callback<UserDto>() {
//            @Override
//            public void onResponse(Call<UserDto> call, Response<UserDto> response) {
//
//            }
//
//            @Override
//            public void onFailure(Call<UserDto> call, Throwable t) {
//
//            }
//        });
    }

    public void loadCurrentUser() throws Exception {
        UserApi userApi = ApiClient.getClient().create(UserApi.class);
        SharedPreferences sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt", null);

        if (token == null) {
            throw new Exception("User not authenticated");
        }

        String userIdString = sharedPreferences.getString("id", null);
        Long userId = userIdString != null ? Long.parseLong(userIdString) : null;

        userApi.getUserById("Bearer " + token, userId).enqueue(new Callback<UserDto>() {
            @Override
            public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                UserStorage.getInstance().setCurrentUser(response.body());
                Toast.makeText(context, "User profile loaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<UserDto> call, Throwable t) {
                Toast.makeText(context, "Failed to load user profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void searchFilterUsers(String fullName) throws Exception {
        UserApi userApi = ApiClient.getClient().create(UserApi.class);
        SharedPreferences sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt", null);

        if (token == null) {
            throw new Exception("User not authenticated");
        }

        userApi.getUsersByFullName("Bearer " + token, fullName).enqueue(new Callback<List<UserDto>>() {
            @Override
            public void onResponse(Call<List<UserDto>> call, Response<List<UserDto>> response) {
                if (!response.isSuccessful())
                    Toast.makeText(context, "User fetching failed: " + response.code(), Toast.LENGTH_SHORT).show();
                else
                    RideHistoryStorage.getInstance().setFilterUsers(response.body());
            }

            @Override
            public void onFailure(Call<List<UserDto>> call, Throwable t) {
                Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
