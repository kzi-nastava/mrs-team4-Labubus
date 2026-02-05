package com.example.ubre.ui.services;

import com.example.ubre.ui.dtos.LoginDto;
import com.example.ubre.ui.dtos.LoginTokenDto;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface LoginService {
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST(ServiceUtils.LOGIN)
    Call<LoginTokenDto> login(@Body LoginDto loginDto);

    @GET(ServiceUtils.LOGOUT)
    Call<ResponseBody> logout(@Header("Authorization") String authHeader);
}
