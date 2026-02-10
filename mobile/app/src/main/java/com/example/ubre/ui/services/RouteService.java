package com.example.ubre.ui.services;

import android.graphics.Color;
import android.util.Log;

import com.example.ubre.ui.apis.OSRMApi;
import com.example.ubre.ui.dtos.WaypointDto;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RouteService {

    private static final String TAG = "RouteService";
    private static final String OSRM_BASE_URL = "https://router.project-osrm.org/";

    private static RouteService instance;
    private final OSRMApi osrmApi;

    private RouteService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(OSRM_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        osrmApi = retrofit.create(OSRMApi.class);
    }

    public static RouteService getInstance() {
        if (instance == null) {
            instance = new RouteService();
        }
        return instance;
    }

    public void drawRoute(MapView mapView, List<WaypointDto> waypoints) {
        if (waypoints == null || waypoints.size() < 2) {
            Log.w(TAG, "Need at least 2 waypoints to draw a route");
            return;
        }

        StringBuilder coordsBuilder = new StringBuilder();
        for (int i = 0; i < waypoints.size(); i++) {
            if (i > 0) coordsBuilder.append(";");
            coordsBuilder.append(waypoints.get(i).getLongitude())
                    .append(",")
                    .append(waypoints.get(i).getLatitude());
        }

        osrmApi.getRoute(coordsBuilder.toString(), "full", "geojson")
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            Log.e(TAG, "OSRM response error: " + response.code());
                            return;
                        }

                        try {
                            JsonArray routes = response.body().getAsJsonArray("routes");
                            if (routes.size() == 0) {
                                Log.w(TAG, "No routes found");
                                return;
                            }

                            JsonArray coordinates = routes.get(0).getAsJsonObject()
                                    .getAsJsonObject("geometry")
                                    .getAsJsonArray("coordinates");

                            List<GeoPoint> routePoints = new ArrayList<>();
                            for (JsonElement element : coordinates) {
                                JsonArray coord = element.getAsJsonArray();
                                double lng = coord.get(0).getAsDouble();
                                double lat = coord.get(1).getAsDouble();
                                routePoints.add(new GeoPoint(lat, lng));
                            }

                            mapView.post(() -> {
                                Polyline polyline = new Polyline();
                                polyline.setPoints(routePoints);
                                polyline.getOutlinePaint().setColor(Color.parseColor("#1565C0"));
                                polyline.getOutlinePaint().setStrokeWidth(10f);
                                polyline.getOutlinePaint().setAntiAlias(true);

                                mapView.getOverlays().add(0, polyline);
                                mapView.invalidate();
                            });

                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing OSRM response", e);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.e(TAG, "OSRM request failed", t);
                    }
                });
    }
}
