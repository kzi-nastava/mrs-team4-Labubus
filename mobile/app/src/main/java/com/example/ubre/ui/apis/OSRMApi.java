package com.example.ubre.ui.apis;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OSRMApi {

    // coordinates format: "lng1,lat1;lng2,lat2;lng3,lat3"
    @GET("route/v1/driving/{coordinates}")
    Call<JsonObject> getRoute(
            @Path("coordinates") String coordinates,
            @Query("overview") String overview,
            @Query("geometries") String geometries
    );
}
