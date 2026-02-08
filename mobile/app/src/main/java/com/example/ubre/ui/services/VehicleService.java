package com.example.ubre.ui.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.ubre.ui.apis.ApiClient;
import com.example.ubre.ui.apis.VehicleApi;
import com.example.ubre.ui.dtos.RideDto;
import com.example.ubre.ui.dtos.VehicleDto;
import com.example.ubre.ui.storages.RideDetailsStorage;
import com.example.ubre.ui.storages.UserStorage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VehicleService {

    private static VehicleService instance;

    private VehicleApi api;

    private VehicleService() {
        api = ApiClient.getClient().create(VehicleApi.class);
    }

    public static VehicleService getInstance() {
        if (instance == null)
            instance = new VehicleService();
        return  instance;
    }

    public void getSelectedRideVehicle(Context context) throws Exception {
        SharedPreferences sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt", null);

        if (token == null) {
            throw new Exception("User not authenticated");
        }

        Callback<VehicleDto> callback = new Callback<VehicleDto>() {
            @Override
            public void onResponse(Call<VehicleDto> call, Response<VehicleDto> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(context, "Vehicle fetching failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    RideDetailsStorage.getInstance().setSelectedRideVehicle(null);
                }
                else
                    RideDetailsStorage.getInstance().setSelectedRideVehicle(response.body());
            }

            @Override
            public void onFailure(Call<VehicleDto> call, Throwable t) {
                Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("VEHICLE FETCH", t.getMessage());
            }
        };

        if (RideDetailsStorage.getInstance().getSelectedRideReadOnly().getValue() != null && RideDetailsStorage.getInstance().getSelectedRideReadOnly().getValue().getDriver() != null)
            api.getVehicleByDriver("Bearer " + token, RideDetailsStorage.getInstance().getSelectedRideReadOnly().getValue().getDriver().getId()).enqueue(callback);
    }
}
