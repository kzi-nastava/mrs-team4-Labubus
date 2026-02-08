package com.example.ubre.ui.apis;

import com.example.ubre.ui.dtos.ReviewDto;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ReviewApi {
    @Headers({
            "User-Agent: Mobile-Android",
    })
    @POST("api/reviews/ride/{rideId}")
    Call<ResponseBody> createReview(@Header("Authorization") String authHeader, @Path("rideId") Long id, @Body ReviewDto review);
}
