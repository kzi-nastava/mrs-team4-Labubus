package com.example.ubre.ui.services;

import android.content.Context;

import com.example.ubre.ui.apis.GeocodingApi;
import com.example.ubre.ui.dtos.GeocodingResult;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GeocodingService {

    public interface GeocodingCallback {
        void onResult(GeocodingResult result);
        void onError(Throwable t);
    }

    private static GeocodingService instance;
    private final GeocodingApi api;

    private GeocodingService(Context context) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request().newBuilder()
                            .header("User-Agent", context.getPackageName())
                            .build();
                    return chain.proceed(request);
                })
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://nominatim.openstreetmap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        api = retrofit.create(GeocodingApi.class);
    }

    public static synchronized GeocodingService getInstance(Context context) {
        if (instance == null) {
            instance = new GeocodingService(context.getApplicationContext());
        }
        return instance;
    }

    public void geocode(String query, GeocodingCallback callback) {
        api.search(query, "json", 1, 1, "en")
                .enqueue(new Callback<List<GeocodingResult>>() {
                    @Override
                    public void onResponse(Call<List<GeocodingResult>> call, Response<List<GeocodingResult>> response) {
                        if (!response.isSuccessful() || response.body() == null || response.body().isEmpty()) {
                            callback.onResult(null);
                            return;
                        }
                        callback.onResult(response.body().get(0));
                    }

                    @Override
                    public void onFailure(Call<List<GeocodingResult>> call, Throwable t) {
                        callback.onError(t);
                    }
                });
    }

    public void reverse(double lat, double lon, GeocodingCallback callback) {
        api.reverse(lat, lon, "json", 18, 1, "en")
                .enqueue(new Callback<GeocodingResult>() {
                    @Override
                    public void onResponse(Call<GeocodingResult> call, Response<GeocodingResult> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            callback.onResult(null);
                            return;
                        }
                        callback.onResult(response.body());
                    }

                    @Override
                    public void onFailure(Call<GeocodingResult> call, Throwable t) {
                        callback.onError(t);
                    }
                });
    }
}
