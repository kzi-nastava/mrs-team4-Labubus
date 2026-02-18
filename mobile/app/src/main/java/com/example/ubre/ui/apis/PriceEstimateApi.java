package com.example.ubre.ui.apis;

import com.example.ubre.ui.dtos.PriceEstimateRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface PriceEstimateApi {
    @POST("api/rides/price-estimate")
    Call<Double> estimatePrice(@Header("Authorization") String authHeader, @Body PriceEstimateRequest request);
}
