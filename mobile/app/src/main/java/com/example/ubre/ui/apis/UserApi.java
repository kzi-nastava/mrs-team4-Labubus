package com.example.ubre.ui.apis;

import com.example.ubre.ui.dtos.UserDto;
import com.example.ubre.ui.dtos.VehicleDto;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
public interface UserApi {
    @Headers({
            "User-Agent: Mobile-Android",
    })
    @GET("api/users/{id}")
    Call<UserDto> getUserById(@Header("Authorization") String authHeader, @Path("id") Long id);

    // get user avatar
    @GET("api/users/{id}/avatar")
    Call<ResponseBody> getUserAvatar(@Header("Authorization") String authHeader, @Path("id") Long id);

    // get user drivers vehicle
    @GET("api/vehicles/driver/{id}")
    Call<VehicleDto> getUserVehicle(@Header("Authorization") String authHeader, @Path("id") Long id);
}
