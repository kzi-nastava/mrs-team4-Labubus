package com.example.ubre.ui.apis;

import com.example.ubre.ui.dtos.VehicleDto;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface VehicleApi {
    @Headers({
            "User-Agent: Mobile-Android",
    })
    @GET("api/vehicles/driver/{driverId}")
    Call<VehicleDto> getVehicleByDriver(@Header("Authorization") String authHeader, @Path("driverId") Long driverId);
}
