package com.example.ubre.ui.apis;

import com.example.ubre.ui.dtos.VehicleTypePricingDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PUT;

public interface PricingApi {
    @Headers({ "User-Agent: Mobile-Android" })
    @GET("api/pricing")
    Call<VehicleTypePricingDto> getPricing(@Header("Authorization") String authHeader);

    @Headers({ "User-Agent: Mobile-Android" })
    @PUT("api/pricing")
    Call<VehicleTypePricingDto> updatePricing(@Header("Authorization") String authHeader, @Body VehicleTypePricingDto dto);
}
