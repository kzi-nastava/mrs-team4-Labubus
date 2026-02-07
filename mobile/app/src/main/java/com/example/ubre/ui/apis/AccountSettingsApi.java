package com.example.ubre.ui.apis;

import com.example.ubre.ui.dtos.UserDto;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface AccountSettingsApi {
    // update user profile (for admins and registered users)
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT("api/users/{id}")
    Call<UserDto> updateUserProfile(@Header("Authorization") String authHeader, @Path("id") Long id, @Body UserDto userDto);

    // update user avatar (backend receives multipart form data file)
    @Headers({
            "User-Agent: Mobile-Android",
    })
    @Multipart
    @POST("api/users/{id}/avatar")
    Call<Void> updateUserAvatar(@Header("Authorization") String authHeader, @Path("id") Long id, @Part MultipartBody.Part file);
}
