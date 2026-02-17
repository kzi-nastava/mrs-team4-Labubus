package com.example.ubre.ui.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.ubre.ui.apis.ApiClient;
import com.example.ubre.ui.apis.ReportsApi;
import com.example.ubre.ui.dtos.ReportsRequestDto;
import com.example.ubre.ui.dtos.ReportsResponseDto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportsService {
    private static ReportsService instance;
    private final Context context;

    private ReportsService(Context context) {
        this.context = context.getApplicationContext();
    }

    public static ReportsService getInstance(Context context) {
        if (instance == null) {
            instance = new ReportsService(context);
        }
        return instance;
    }

    public void loadReports(ReportsRequestDto request, Callback<ReportsResponseDto> callback) throws Exception {
        ReportsApi api = ApiClient.getClient().create(ReportsApi.class);
        SharedPreferences sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt", null);

        if (token == null) {
            throw new Exception("User not authenticated");
        }

        api.getReports("Bearer " + token, request).enqueue(new Callback<ReportsResponseDto>() {
            @Override
            public void onResponse(Call<ReportsResponseDto> call, Response<ReportsResponseDto> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(context, "Reports load failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<ReportsResponseDto> call, Throwable t) {
                Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                callback.onFailure(call, t);
            }
        });
    }
}
