package com.example.ubre.ui.apis;

import com.example.ubre.ui.dtos.RideCardDto;
import com.example.ubre.ui.dtos.RideDto;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface RideApi {
    @Headers({
            "User-Agent: Mobile-Android",
    })
    @GET("api/rides/{id}")
    Call<RideDto> getRideById(@Header("Authorization") String authHeader, @Path("id") Long id);

    @GET("api/rides/history")
    Call<List<RideCardDto>> getRideHistory(@Header("Authorization") String authHeader, @Query("skip") Integer skip, @Query("count") Integer count, @Query("sortBy") String sort, @Query("ascending") Boolean ascending, @Query("date") LocalDateTime date);

    @GET("api/rides/history/{id}")
    Call<List<RideCardDto>> getRideHistory(@Header("Authorization") String authHeader, @Path("id") Long id, @Query("skip") Integer skip, @Query("count") Integer count, @Query("sortBy") String sort, @Query("ascending") Boolean ascending, @Query("date") LocalDateTime date);

    @PUT("api/rides/{userId}/favorites/{rideId}")
    Call<ResponseBody> addToFavorites(@Header("Authorization") String authHeader, @Path("userId") Long userId, @Path("rideId") Long rideId);

    @DELETE("api/rides/{userId}/favorites/{rideId}")
    Call<ResponseBody> removeFromFavorites(@Header("Authorization") String authHeader, @Path("userId") Long userId, @Path("rideId") Long rideId);
}
