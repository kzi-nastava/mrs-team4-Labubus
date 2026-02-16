package com.example.ubre.ui.services;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.ubre.ui.apis.ApiClient;
import com.example.ubre.ui.apis.PricingApi;
import com.example.ubre.ui.dtos.VehicleTypePricingDto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PricingService {
    private static PricingService instance;
    private final Context context;
    private final PricingApi pricingApi;

    public interface PricingCallback {
        void onSuccess(VehicleTypePricingDto pricing);
        void onError(String message);
    }

    private PricingService(Context context) {
        this.context = context.getApplicationContext();
        this.pricingApi = ApiClient.getClient().create(PricingApi.class);
    }

    public static PricingService getInstance(Context context) {
        if (instance == null) {
            instance = new PricingService(context);
        }
        return instance;
    }

    private String getAuthHeader() {
        SharedPreferences sp = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sp.getString("jwt", null);
        return token != null ? "Bearer " + token : "";
    }

    public void getPricing(PricingCallback callback) {
        pricingApi.getPricing(getAuthHeader()).enqueue(new Callback<VehicleTypePricingDto>() {
            @Override
            public void onResponse(Call<VehicleTypePricingDto> call, Response<VehicleTypePricingDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to load pricing");
                }
            }

            @Override
            public void onFailure(Call<VehicleTypePricingDto> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void updatePricing(VehicleTypePricingDto dto, PricingCallback callback) {
        pricingApi.updatePricing(getAuthHeader(), dto).enqueue(new Callback<VehicleTypePricingDto>() {
            @Override
            public void onResponse(Call<VehicleTypePricingDto> call, Response<VehicleTypePricingDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to update pricing");
                }
            }

            @Override
            public void onFailure(Call<VehicleTypePricingDto> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}
