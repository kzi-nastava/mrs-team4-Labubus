package com.example.ubre.ui.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.ubre.ui.apis.ApiClient;
import com.example.ubre.ui.apis.ReviewApi;
import com.example.ubre.ui.dtos.ReviewDto;
import com.example.ubre.ui.dtos.RideDto;
import com.example.ubre.ui.storages.ReviewStorage;
import com.example.ubre.ui.storages.RideDetailsStorage;
import com.example.ubre.ui.storages.UserStorage;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewService {
    private static ReviewService instance;

    private ReviewApi api;

    private ReviewService() {
        api = ApiClient.getClient().create(ReviewApi.class);
    }

    public static ReviewService getInstance() {
        if (instance == null)
            instance = new ReviewService();
        return instance;
    }

    public void createReview(Context context, Long rideId, ReviewDto review) throws Exception {
        SharedPreferences sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt", null);

        if (token == null) {
            throw new Exception("User not authenticated");
        }

        Callback<ResponseBody> callback = new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(context, "Review submission failed: " + response.body(), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(context, "Review successfully submitted", Toast.LENGTH_SHORT).show();
                    ReviewStorage.getInstance().setRideId(null);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("RIDE FETCH", t.getMessage());
            }
        };

        api.createReview("Bearer " + token, rideId, review).enqueue(callback);
    }
}
