package com.example.ubre.ui.apis;

import com.example.ubre.ui.dtos.ReportsRequestDto;
import com.example.ubre.ui.dtos.ReportsResponseDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ReportsApi {
    @Headers({
            "User-Agent: Mobile-Android",
    })
    @POST("api/reports")
    Call<ReportsResponseDto> getReports(@Header("Authorization") String authHeader, @Body ReportsRequestDto body);
}
