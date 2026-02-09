package com.example.ubre.ui.apis;

import com.example.ubre.ui.dtos.UserStatsDto;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface UserStatsApi {
    // interface for user statistics, and similar stuff
    @Headers({
            "User-Agent: Mobile-Android",
    })
    @GET("api/users/{id}/stats")
    Call<UserStatsDto> getUserStats(@Header("Authorization") String authHeader, @Path("id") Long id);
}
