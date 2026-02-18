package com.example.ubre.ui.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.ubre.ui.apis.ApiClient;
import com.example.ubre.ui.apis.RideApi;
import com.example.ubre.ui.dtos.RideCardDto;
import com.example.ubre.ui.dtos.RideDto;
import com.example.ubre.ui.dtos.RideOrderRequest;
import com.example.ubre.ui.dtos.WaypointDto;
import com.example.ubre.ui.enums.RideStatus;
import com.example.ubre.ui.utils.TopToast;
import com.example.ubre.ui.storages.RideDetailsStorage;
import com.example.ubre.ui.storages.RideHistoryStorage;
import com.example.ubre.ui.storages.UserStorage;
import com.example.ubre.ui.storages.CurrentRideStorage;
import com.example.ubre.ui.storages.FavoriteRidesStorage;
import com.google.gson.Gson;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import org.json.JSONObject;

public class RideService {
    private static RideService instance;

    private RideApi api;
    private final Gson gson = new Gson();

    private RideService() {
        this.api = ApiClient.getClient().create(RideApi.class);
    }

    public static RideService getInstance() {
        if (instance == null)
            instance = new RideService();
        return instance;
    }

    public void getHistoryPage(Context context, Long userId, Integer skip, Integer count, String sortBy, Boolean ascending, LocalDate date) throws Exception {
        SharedPreferences sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt", null);
        String role = sharedPreferences.getString("role", "");

        if (token == null) {
            throw new Exception("User not authenticated");
        }

        Callback<List<RideCardDto>> callback = new Callback<List<RideCardDto>>() {
            @Override
            public void onResponse(Call<List<RideCardDto>> call, Response<List<RideCardDto>> response) {
                if (!response.isSuccessful())
                    Toast.makeText(context, "History fetching failed: " + response.code(), Toast.LENGTH_SHORT).show();
                else
                    RideHistoryStorage.getInstance().extendHistory(response.body());
            }

            @Override
            public void onFailure(Call<List<RideCardDto>> call, Throwable t) {
                Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("HISTORY FETCH", t.getMessage());
            }
        };

        if (userId != null && role.equals("ADMIN"))
            api.getRideHistory("Bearer " + token, userId, skip, count, sortBy, ascending, date == null ? null : date.atStartOfDay()).enqueue(callback);
        else if (role.equals("ADMIN"))
            api.getRideHistory("Bearer " + token, skip, count, sortBy, ascending, date == null ? null : date.atStartOfDay()).enqueue(callback);
        else
            api.getRideHistory("Bearer " + token, UserStorage.getInstance().getCurrentUser().getValue().getId(), skip, count, sortBy, ascending, date == null ? null : date.atStartOfDay()).enqueue(callback);
    }

    public void getRideDetails(Context context, Long id) throws Exception {
        SharedPreferences sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt", null);

        if (token == null) {
            throw new Exception("User not authenticated");
        }

        Callback<RideDto> callback = new Callback<RideDto>() {
            @Override
            public void onResponse(Call<RideDto> call, Response<RideDto> response) {
                if (!response.isSuccessful())
                    Toast.makeText(context, "Ride fetching failed: " + response.code(), Toast.LENGTH_SHORT).show();
                else
                    RideDetailsStorage.getInstance().setSelectedRide(response.body());
            }

            @Override
            public void onFailure(Call<RideDto> call, Throwable t) {
                Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("RIDE FETCH", t.getMessage());
            }
        };

        api.getRideById("Bearer " + token, id).enqueue(callback);
    }

    public void getCurrentRide(Context context) throws Exception {
        SharedPreferences sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt", null);

        if (token == null) {
            throw new Exception("User not authenticated");
        }

        Callback<ResponseBody> callback = new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 404) {
                    CurrentRideStorage.getInstance().clear();
                    return;
                }
                if (!response.isSuccessful()) {
                    Toast.makeText(context, "Current ride fetch failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                ResponseBody body = response.body();
                if (body == null) {
                    CurrentRideStorage.getInstance().clear();
                    return;
                }
                String raw;
                try {
                    raw = body.string();
                } catch (Exception e) {
                    Toast.makeText(context, "Current ride parse error", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (raw == null || raw.trim().isEmpty()) {
                    CurrentRideStorage.getInstance().clear();
                    return;
                }
                try {
                    RideDto ride = gson.fromJson(raw, RideDto.class);
                    if (ride != null) {
                        CurrentRideStorage.getInstance().setCurrentRide(ride);
                    } else {
                        CurrentRideStorage.getInstance().clear();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "Current ride parse error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("CURRENT RIDE FETCH", t.getMessage());
            }
        };

        api.getCurrentRide("Bearer " + token).enqueue(callback);
    }

    public void toggleFavorite(Context context, RideCardDto ride) throws Exception {
        SharedPreferences sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt", null);

        if (token == null) {
            throw new Exception("User not authenticated");
        }

        if (UserStorage.getInstance().getCurrentUser().getValue() == null)
            throw  new Exception("Unable to favorite/unfavorite ride");

        Long userId = UserStorage.getInstance().getCurrentUser().getValue().getId();
        Callback<ResponseBody> callback = new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful())
                    Toast.makeText(context, "Failed to favorite/unfavorite ride: " + response.code(), Toast.LENGTH_SHORT).show();
                else {
                    boolean newFavoriteState = !Boolean.TRUE.equals(ride.favorite);

                    List<RideCardDto> history = RideHistoryStorage.getInstance().getHistoryReadOnly().getValue();
                    if (history != null) {
                        List<RideCardDto> updatedHistory = new ArrayList<>(history.size());
                        for (RideCardDto card : history) {
                            boolean isTarget = card.getId().equals(ride.getId());
                            updatedHistory.add(copyRideCard(card, isTarget ? newFavoriteState : card.favorite));
                        }
                        RideHistoryStorage.getInstance().setHistory(updatedHistory);
                    }

                    List<RideCardDto> favorites = FavoriteRidesStorage.getInstance().getFavoritesReadOnly().getValue();
                    if (favorites != null) {
                        List<RideCardDto> updatedFavorites = new ArrayList<>();
                        for (RideCardDto card : favorites) {
                            if (!card.getId().equals(ride.getId())) {
                                updatedFavorites.add(copyRideCard(card, card.favorite));
                            }
                        }
                        if (newFavoriteState) {
                            updatedFavorites.add(copyRideCard(ride, true));
                        }
                        FavoriteRidesStorage.getInstance().setFavorites(updatedFavorites);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TOGGLE RIDE FAVORITE", t.getMessage());
            }
        };

        if (ride.favorite)
            api.removeFromFavorites("Bearer " + token, userId, ride.getId()).enqueue(callback);
        else
            api.addToFavorites("Bearer " + token, userId, ride.getId()).enqueue(callback);
    }

    public void getFavorites(Context context, Long userId) throws Exception {
        SharedPreferences sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt", null);

        if (token == null) {
            throw new Exception("User not authenticated");
        }

        Long resolvedUserId = userId;
        if (resolvedUserId == null && UserStorage.getInstance().getCurrentUser().getValue() != null) {
            resolvedUserId = UserStorage.getInstance().getCurrentUser().getValue().getId();
        }
        if (resolvedUserId == null) {
            throw new Exception("User not found");
        }

        api.getFavoriteRides("Bearer " + token, resolvedUserId).enqueue(new Callback<List<RideCardDto>>() {
            @Override
            public void onResponse(Call<List<RideCardDto>> call, Response<List<RideCardDto>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(context, "Favorites fetching failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                FavoriteRidesStorage.getInstance().setFavorites(response.body() == null ? List.of() : response.body());
            }

            @Override
            public void onFailure(Call<List<RideCardDto>> call, Throwable t) {
                Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("FAVORITES FETCH", t.getMessage());
            }
        });
    }

    public interface OrderCallback {
        void onSuccess(RideDto ride);
        void onError(String message);
    }

    public void orderRide(Context context, RideOrderRequest request, OrderCallback callback) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt", null);

        if (token == null) {
            callback.onError("Unauthorized. Please login again.");
            return;
        }

        api.orderRide("Bearer " + token, request).enqueue(new Callback<RideDto>() {
            @Override
            public void onResponse(Call<RideDto> call, Response<RideDto> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError(buildOrderErrorMessage(response, null));
                    return;
                }
                RideDetailsStorage.getInstance().setSelectedRide(response.body());
                callback.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<RideDto> call, Throwable t) {
                callback.onError(buildOrderErrorMessage(null, t));
            }
        });
    }

    public void startRide(Context context, Long rideId) throws Exception {
        SharedPreferences sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt", null);

        if (token == null) {
            throw new Exception("User not authenticated");
        }
        if (rideId == null || rideId == 0L) {
            throw new Exception("Invalid ride id");
        }

        api.startRide("Bearer " + token, rideId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(context, "Start ride failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                RideDto current = CurrentRideStorage.getInstance().getCurrentRideReadOnly().getValue();
                if (current != null) {
                    current.setStatus(RideStatus.IN_PROGRESS);
                    CurrentRideStorage.getInstance().setCurrentRide(current);
                }
                TopToast.show(context, "Ride started", "Please drive carefully and enjoy your ride.");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("START RIDE", t.getMessage());
            }
        });
    }

    private String buildOrderErrorMessage(Response<?> response, Throwable t) {
        if (response != null) {
            int code = response.code();
            if (code == 401) return "Unauthorized. Please login again.";
            if (code == 403) return "Forbidden. You are not authorized to access this resource.";

            String body = readErrorBody(response.errorBody());
            if (body != null && !body.trim().isEmpty()) {
                String detail = tryGetDetail(body);
                if (detail != null) return detail;
                if (!looksLikeJson(body)) return body.trim();
            }

            String reason = body != null && !body.trim().isEmpty() ? body.trim() : response.message();
            return "Order couldn't be completed. " + reason + " (Error " + code + ").";
        }

        String reason = (t != null && t.getMessage() != null) ? t.getMessage() : "Unknown error";
        return "Order couldn't be completed. " + reason + " (Error 0).";
    }

    private String readErrorBody(ResponseBody errorBody) {
        if (errorBody == null) return null;
        try {
            return errorBody.string();
        } catch (Exception e) {
            return null;
        }
    }

    private String tryGetDetail(String body) {
        try {
            JSONObject obj = new JSONObject(body);
            if (obj.has("detail")) return obj.getString("detail");
        } catch (Exception ignored) {
        }
        return null;
    }

    private boolean looksLikeJson(String body) {
        String s = body.trim();
        return s.startsWith("{") || s.startsWith("[");
    }

    private RideCardDto copyRideCard(RideCardDto source, Boolean favoriteOverride) {
        String start = source.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        List<WaypointDto> waypoints = source.getWaypoints() == null ? List.of() : new ArrayList<>(source.getWaypoints());
        Boolean fav = favoriteOverride != null ? favoriteOverride : source.favorite;
        return new RideCardDto(source.getId(), start, waypoints, fav);
    }
}
