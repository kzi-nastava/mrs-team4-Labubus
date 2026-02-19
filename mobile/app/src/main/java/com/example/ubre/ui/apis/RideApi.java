package com.example.ubre.ui.apis;

import com.example.ubre.ui.dtos.CancellationDto;
import com.example.ubre.ui.dtos.PanicNotificationDto;
import com.example.ubre.ui.dtos.RideCardDto;
import com.example.ubre.ui.dtos.RideDto;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Body;

import java.time.LocalDateTime;
import java.util.List;
import com.example.ubre.ui.dtos.RideOrderRequest;
import com.example.ubre.ui.dtos.WaypointDto;

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

    @GET("api/rides/{userId}/favorites")
    Call<List<RideCardDto>> getFavoriteRides(@Header("Authorization") String authHeader, @Path("userId") Long userId);

    @POST("api/rides/order")
    Call<RideDto> orderRide(@Header("Authorization") String authHeader, @Body RideOrderRequest request);

    @GET("api/rides/current")
    Call<ResponseBody> getCurrentRide(@Header("Authorization") String authHeader);

    @POST("api/rides/{id}/start")
    Call<ResponseBody> startRide(@Header("Authorization") String authHeader, @Path("id") Long id);

    @PUT("api/rides/{id}/stop")
    Call<ResponseBody> stopRide(@Header("Authorization") String authHeader, @Path("id") Long id, @Body WaypointDto waypoint);
    
    @POST("api/rides/{id}/panic")
    Call<ResponseBody> activatePanic(@Header("Authorization") String authHeader, @Path("id") Long id);

    @GET("api/rides/panic")
    Call<List<PanicNotificationDto>> getPanics(@Header("Authorization") String authHeader);

    @GET("api/rides/scheduled/user/{userId}")
    Call<List<RideCardDto>> getScheduledRides(
            @Header("Authorization") String authHeader,
            @Path("userId") Long userId,
            @Query("skip") Integer skip,
            @Query("count") Integer count
    );

    @PUT("api/rides/{rideId}/cancel/user")
    Call<Void> cancelRide(@Header("Authorization") String authHeader, @Path("rideId") Long rideId);

    @PUT("api/rides/{rideId}/cancel/driver")
    Call<RideDto> cancelRideDriver(@Header("Authorization") String authHeader,
                                   @Path("rideId") Long rideId,
                                   @Body CancellationDto body);
}
