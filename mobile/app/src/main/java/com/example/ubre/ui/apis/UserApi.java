package com.example.ubre.ui.apis;

import com.example.ubre.ui.dtos.UserDto;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
public interface UserApi {
    @GET("api/users/{id}")
    Call<UserDto> getUserById(@Header("Authorization") String authHeader, @Path("id") Long id);

    // get user avatar
    @GET("api/users/{id}/avatar") // on frontend, we used blob, but now we use response body to get the image
    Call<ResponseBody> getUserAvatar(@Path("id") Long id);
}
