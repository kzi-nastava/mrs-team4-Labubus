package com.example.ubre.ui.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.ubre.ui.apis.ApiClient;
import com.example.ubre.ui.apis.RideApi;
import com.example.ubre.ui.dtos.RideCardDto;
import com.example.ubre.ui.dtos.RideDto;
import com.example.ubre.ui.storages.RideDetailsStorage;
import com.example.ubre.ui.storages.RideHistoryStorage;
import com.example.ubre.ui.storages.UserStorage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideService {
    private static RideService instance;

    private RideApi api;

    private RideService() {
        this.api = ApiClient.getClient().create(RideApi.class);
    }

    public static RideService getInstance() {
        if (instance == null)
            instance = new RideService();
        return instance;
    }

    public void getHistoryPage(Context context, Long userId, Integer skip, Integer count, String sortBy, Boolean ascending, LocalDate date) throws Exception {
        SharedPreferences sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt", null);
        String role = sharedPreferences.getString("role", "");

        if (token == null) {
            throw new Exception("User not authenticated");
        }

        Callback<List<RideCardDto>> callback = new Callback<List<RideCardDto>>() {
            @Override
            public void onResponse(Call<List<RideCardDto>> call, Response<List<RideCardDto>> response) {
                if (!response.isSuccessful())
                    Toast.makeText(context, "History fetching failed: " + response.code(), Toast.LENGTH_SHORT).show();
                else
                    RideHistoryStorage.getInstance().extendHistory(response.body());
            }

            @Override
            public void onFailure(Call<List<RideCardDto>> call, Throwable t) {
                Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("HISTORY FETCH", t.getMessage());
            }
        };

        if (userId != null && role.equals("ADMIN"))
            api.getRideHistory("Bearer " + token, userId, skip, count, sortBy, ascending, date == null ? null : date.atStartOfDay()).enqueue(callback);
        else if (role.equals("ADMIN"))
            api.getRideHistory("Bearer " + token, skip, count, sortBy, ascending, date == null ? null : date.atStartOfDay()).enqueue(callback);
        else
            api.getRideHistory("Bearer " + token, UserStorage.getInstance().getCurrentUser().getValue().getId(), skip, count, sortBy, ascending, date == null ? null : date.atStartOfDay()).enqueue(callback);
    }

    public void getRideDetails(Context context, Long id) throws Exception {
        SharedPreferences sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt", null);

        if (token == null) {
            throw new Exception("User not authenticated");
        }

        Callback<RideDto> callback = new Callback<RideDto>() {
            @Override
            public void onResponse(Call<RideDto> call, Response<RideDto> response) {
                if (!response.isSuccessful())
                    Toast.makeText(context, "Ride fetching failed: " + response.code(), Toast.LENGTH_SHORT).show();
                else
                    RideDetailsStorage.getInstance().setSelectedRide(response.body());
            }

            @Override
            public void onFailure(Call<RideDto> call, Throwable t) {
                Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("RIDE FETCH", t.getMessage());
            }
        };

        api.getRideById("Bearer " + token, id).enqueue(callback);
    }
}
