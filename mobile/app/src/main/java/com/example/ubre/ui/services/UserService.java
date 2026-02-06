package com.example.ubre.ui.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.ubre.ui.apis.ApiClient;
import com.example.ubre.ui.apis.UserApi;
import com.example.ubre.ui.dtos.UserDto;
import com.example.ubre.ui.main.MainActivity;

import android.content.Context;
import android.content.SharedPreferences;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserService {
    private static final String TAG = "UserService";
    private static UserService instance;

    private UserService() {
    }

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
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
}
