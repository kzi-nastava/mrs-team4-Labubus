package com.example.ubre.ui.apis;

import com.example.ubre.ui.dtos.ComplaintDto;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ComplaintApi {
    @Headers({
            "User-Agent: Mobile-Android",
    })
    @POST("api/complaints/ride/{rideId}")
    Call<ResponseBody> createComplaint(@Header("Authorization") String authHeader, @Path("rideId") Long id, @Body ComplaintDto complaint);
}
