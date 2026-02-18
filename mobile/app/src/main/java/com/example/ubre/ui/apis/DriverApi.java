package com.example.ubre.ui.apis;

import com.example.ubre.ui.dtos.DriverRegistrationDto;
import com.example.ubre.ui.dtos.UserDto;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface DriverApi {
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST("api/drivers")
    Call<UserDto> registerDriver(@Header("Authorization") String authHeader,
                                 @Body DriverRegistrationDto registrationDto);

    @Headers({
            "User-Agent: Mobile-Android",
    })
    @Multipart
    @POST("api/users/{driverId}/avatar")
    Call<Void> uploadDriverAvatar(@Header("Authorization") String authHeader,
                                  @Path("driverId") Long driverId,
                                  @Part MultipartBody.Part file);
}
