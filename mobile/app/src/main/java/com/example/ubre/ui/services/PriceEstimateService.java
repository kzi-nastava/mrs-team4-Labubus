package com.example.ubre.ui.services;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.ubre.ui.apis.ApiClient;
import com.example.ubre.ui.apis.PriceEstimateApi;
import com.example.ubre.ui.dtos.PriceEstimateRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PriceEstimateService {

    public interface PriceEstimateCallback {
        void onResult(Double price);
        void onError(Throwable t);
    }

    private static PriceEstimateService instance;
    private final PriceEstimateApi api;
    private final Context appContext;

    private PriceEstimateService(Context context) {
        this.appContext = context.getApplicationContext();
        this.api = ApiClient.getClient().create(PriceEstimateApi.class);
    }

    public static synchronized PriceEstimateService getInstance(Context context) {
        if (instance == null) {
            instance = new PriceEstimateService(context);
        }
        return instance;
    }

    public void estimate(double distanceMeters, int vehicleType, PriceEstimateCallback callback) {
        PriceEstimateRequest request = new PriceEstimateRequest(distanceMeters, vehicleType);
        SharedPreferences sharedPreferences = appContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt", null);
        String authHeader = token == null ? null : "Bearer " + token;
        api.estimatePrice(authHeader, request).enqueue(new Callback<Double>() {
            @Override
            public void onResponse(Call<Double> call, Response<Double> response) {
                if (!response.isSuccessful()) {
                    callback.onResult(null);
                    return;
                }
                callback.onResult(response.body());
            }

            @Override
            public void onFailure(Call<Double> call, Throwable t) {
                callback.onError(t);
            }
        });
    }
}
