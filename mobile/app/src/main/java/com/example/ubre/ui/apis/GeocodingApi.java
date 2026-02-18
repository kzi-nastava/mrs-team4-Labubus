package com.example.ubre.ui.apis;

import com.example.ubre.ui.dtos.GeocodingResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GeocodingApi {

    @GET("search")
    Call<List<GeocodingResult>> search(
            @Query("q") String query,
            @Query("format") String format,
            @Query("limit") int limit,
            @Query("addressdetails") int addressDetails,
            @Query("accept-language") String language,
            @Query("countrycodes") String countryCodes,
            @Query("viewbox") String viewbox,
            @Query("bounded") int bounded
    );

    @GET("reverse")
    Call<GeocodingResult> reverse(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("format") String format,
            @Query("zoom") int zoom,
            @Query("addressdetails") int addressDetails,
            @Query("accept-language") String language
    );
}
