package com.example.ubre.ui.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.ubre.ui.apis.ApiClient;
import com.example.ubre.ui.apis.ComplaintApi;
import com.example.ubre.ui.dtos.ComplaintDto;
import com.example.ubre.ui.storages.ComplaintStorage;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComplaintService {
    private static ComplaintService instance;

    private final ComplaintApi api;

    private ComplaintService() {
        api = ApiClient.getClient().create(ComplaintApi.class);
    }

    public static ComplaintService getInstance() {
        if (instance == null)
            instance = new ComplaintService();
        return instance;
    }

    public void createComplaint(Context context, Long rideId, ComplaintDto complaint) throws Exception {
        SharedPreferences sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt", null);

        if (token == null) {
            throw new Exception("User not authenticated");
        }

        Callback<ResponseBody> callback = new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(context, "Complaint submission failed: " + response.code(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Complaint submitted", Toast.LENGTH_SHORT).show();
                    ComplaintStorage.getInstance().setRideId(null);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("COMPLAINT", t.getMessage());
            }
        };

        api.createComplaint("Bearer " + token, rideId, complaint).enqueue(callback);
    }
}
