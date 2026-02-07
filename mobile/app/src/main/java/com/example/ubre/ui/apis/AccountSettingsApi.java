package com.example.ubre.ui.apis;

import com.example.ubre.ui.dtos.UserDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AccountSettingsApi {
    // update user profile (for admins and registered users)
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT("api/users/{id}")
    Call<UserDto> updateUserProfile(@Header("Authorization") String authHeader, @Path("id") Long id, @Body UserDto userDto);
}
