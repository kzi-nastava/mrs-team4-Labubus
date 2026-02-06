package com.example.ubre.ui.apis;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public static final String SERVICE_API_PATH = "http://192.168.72.98:8080/";
    public static final String LOGIN = "api/auth/login";
    public static final String LOGOUT = "api/auth/logout";
    private static Retrofit retrofit;

    public static OkHttpClient createHttpClient(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .addInterceptor(interceptor).build();

        return client;
    }
    public static Retrofit getClient() {  // ovo koristimo samo i ništa više!
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(SERVICE_API_PATH)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(createHttpClient())
                    .build();
        }
        return retrofit;
    }
}
