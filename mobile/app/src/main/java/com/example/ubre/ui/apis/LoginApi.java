package com.example.ubre.ui.apis;

import com.example.ubre.ui.dtos.LoginDto;
import com.example.ubre.ui.dtos.LoginTokenDto;
import com.example.ubre.ui.apis.ApiClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface LoginApi {
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST(ApiClient.LOGIN)
    Call<LoginTokenDto> login(@Body LoginDto loginDto);

    @GET(ApiClient.LOGOUT)
    Call<ResponseBody> logout(@Header("Authorization") String authHeader);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type: application/json"
    })
    @POST(ApiClient.FORGOT_PASSWORD)
    Call<ResponseBody> forgotPassword(@Body String email);
}
