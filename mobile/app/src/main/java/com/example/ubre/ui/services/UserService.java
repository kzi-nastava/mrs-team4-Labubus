package com.example.ubre.ui.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.ubre.ui.apis.ApiClient;
import com.example.ubre.ui.apis.UserApi;
import com.example.ubre.ui.apis.UserStatsApi;
import com.example.ubre.ui.dtos.BlockUserRequest;
import com.example.ubre.ui.dtos.UserDto;
import com.example.ubre.ui.dtos.UserStatsDto;
import com.example.ubre.ui.dtos.VehicleDto;
import com.example.ubre.ui.main.MainActivity;
import com.example.ubre.ui.storages.ProfileCardStorage;
import com.example.ubre.ui.storages.BlockUsersStorage;
import com.example.ubre.ui.storages.RideDetailsStorage;
import com.example.ubre.ui.storages.RideHistoryStorage;
import com.example.ubre.ui.storages.UserStorage;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserService {
    private static final String TAG = "UserService";
    private static UserService instance;
    private Context context;

    private UserService(Context context) {
        this.context = context.getApplicationContext();
    }

    public static UserService getInstance(Context context) {
        if (instance == null) {
            instance = new UserService(context);
        }
        return instance;
    }

    public void loadCurrentUser() throws Exception {
        UserApi userApi = ApiClient.getClient().create(UserApi.class);
        SharedPreferences sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt", null);

        if (token == null) {
            throw new Exception("User not authenticated");
        }

        String userIdString = sharedPreferences.getString("id", null);
        Long userId = userIdString != null ? Long.parseLong(userIdString) : null;

        userApi.getUserById("Bearer " + token, userId).enqueue(new Callback<UserDto>() {
            @Override
            public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                UserDto user = response.body();
                UserStorage.getInstance().setCurrentUser(user);
                if (user != null && Boolean.TRUE.equals(user.getIsBlocked()) && user.getId() != null) {
                    fetchBlockNote(user.getId(), token);
                } else {
                    UserStorage.getInstance().setBlockNote(null);
                }
                Toast.makeText(context, "User profile loaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<UserDto> call, Throwable t) {
                Toast.makeText(context, "Failed to load user profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loadCurrentUserAvatar() throws Exception {
        UserApi userApi = ApiClient.getClient().create(UserApi.class);
        SharedPreferences sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt", null);

        if (token == null) {
            throw new Exception("User not authenticated");
        }

        String userIdString = sharedPreferences.getString("id", null);
        Long userId = userIdString != null ? Long.parseLong(userIdString) : null;

        userApi.getUserAvatar("Bearer " + token, userId).enqueue(new Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        byte[] avatarBytes = response.body().bytes();
                        UserStorage.getInstance().setCurrentUserAvatar(avatarBytes);
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "Error processing avatar data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
                Toast.makeText(context, "Failed to load user avatar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // load drivers vehicle (drivers only role)
    public void loadCurrentUserVehicle() throws Exception {
        UserApi userApi = ApiClient.getClient().create(UserApi.class);
        SharedPreferences sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt", null);

        if (token == null) {
            throw new Exception("User not authenticated");
        }

        String userIdString = sharedPreferences.getString("id", null);
        Long userId = userIdString != null ? Long.parseLong(userIdString) : null;

        userApi.getUserVehicle("Bearer " + token, userId).enqueue(new Callback<VehicleDto>() {
            @Override
            public void onResponse(Call<VehicleDto> call, Response<VehicleDto> response) {
                UserStorage.getInstance().setCurrentUserVehicle(response.body());
            }

            @Override
            public void onFailure(Call<VehicleDto> call, Throwable t) {
                Toast.makeText(context, "Failed to load user's vehicle", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // load user stats data (user stats dto)
    public void loadCurrentUserStats() throws Exception {
        UserStatsApi api = ApiClient.getClient().create(UserStatsApi.class);
        SharedPreferences sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt", null);

        if (token == null) {
            throw new Exception("User not authenticated");
        }

        String userIdString = sharedPreferences.getString("id", null);
        Long userId = userIdString != null ? Long.parseLong(userIdString) : null;

        api.getUserStats("Bearer " + token, userId).enqueue(new Callback<UserStatsDto>() {
            @Override
            public void onResponse(Call<UserStatsDto> call, Response<UserStatsDto> response) {
                UserStorage.getInstance().setCurrentUserStats(response.body());
            }

            @Override
            public void onFailure(Call<UserStatsDto> call, Throwable t) {
                Toast.makeText(context, "Failed to load user's stats", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loadProfileCardAvatar(Long userId, ProfileCardStorage storage) throws Exception {
        UserApi userApi = ApiClient.getClient().create(UserApi.class);
        SharedPreferences sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt", null);

        if (token == null) {
            throw new Exception("User not authenticated");
        }

        userApi.getUserAvatar("Bearer " + token, userId).enqueue(new Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        byte[] avatarBytes = response.body().bytes();
                        storage.setAvatar(avatarBytes);
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "Error processing avatar data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
                Toast.makeText(context, "Failed to load user avatar", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void searchFilterUsers(String fullName) throws Exception {
        UserApi userApi = ApiClient.getClient().create(UserApi.class);
        SharedPreferences sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt", null);

        if (token == null) {
            throw new Exception("User not authenticated");
        }

        userApi.getUsersByFullName("Bearer " + token, fullName).enqueue(new Callback<List<UserDto>>() {
            @Override
            public void onResponse(Call<List<UserDto>> call, Response<List<UserDto>> response) {
                if (!response.isSuccessful())
                    Toast.makeText(context, "User fetching failed: " + response.code(), Toast.LENGTH_SHORT).show();
                else
                    RideHistoryStorage.getInstance().setFilterUsers(response.body());
            }

            @Override
            public void onFailure(Call<List<UserDto>> call, Throwable t) {
                Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface SimpleCallback {
        void onSuccess();
        void onFailure();
    }

    public void loadAllUsers() throws Exception {
        UserApi userApi = ApiClient.getClient().create(UserApi.class);
        SharedPreferences sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt", null);

        if (token == null) {
            throw new Exception("User not authenticated");
        }

        BlockUsersStorage.getInstance().setLoading(true);
        userApi.getAllUsers("Bearer " + token).enqueue(new Callback<List<UserDto>>() {
            @Override
            public void onResponse(Call<List<UserDto>> call, Response<List<UserDto>> response) {
                BlockUsersStorage.getInstance().setLoading(false);
                if (!response.isSuccessful()) {
                    Toast.makeText(context, "Failed to load users.", Toast.LENGTH_SHORT).show();
                    return;
                }
                BlockUsersStorage.getInstance().setUsers(response.body());
            }

            @Override
            public void onFailure(Call<List<UserDto>> call, Throwable t) {
                BlockUsersStorage.getInstance().setLoading(false);
                Toast.makeText(context, "Failed to load users.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void blockUser(Long userId, String note, SimpleCallback callback) throws Exception {
        UserApi userApi = ApiClient.getClient().create(UserApi.class);
        SharedPreferences sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt", null);

        if (token == null) {
            throw new Exception("User not authenticated");
        }

        Object body;
        String trimmed = note == null ? "" : note.trim();
        if (trimmed.isEmpty()) {
            body = new HashMap<>();
        } else {
            body = new BlockUserRequest(trimmed);
        }

        userApi.blockUser("Bearer " + token, userId, body).enqueue(new Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (callback != null) callback.onSuccess();
                } else {
                    if (callback != null) callback.onFailure();
                }
            }

            @Override
            public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
                if (callback != null) callback.onFailure();
            }
        });
    }

    public void unblockUser(Long userId, SimpleCallback callback) throws Exception {
        UserApi userApi = ApiClient.getClient().create(UserApi.class);
        SharedPreferences sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt", null);

        if (token == null) {
            throw new Exception("User not authenticated");
        }

        userApi.unblockUser("Bearer " + token, userId, new HashMap<>()).enqueue(new Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (callback != null) callback.onSuccess();
                } else {
                    if (callback != null) callback.onFailure();
                }
            }

            @Override
            public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
                if (callback != null) callback.onFailure();
            }
        });
    }

    private void fetchBlockNote(Long userId, String token) {
        UserApi userApi = ApiClient.getClient().create(UserApi.class);
        userApi.getBlockNote("Bearer " + token, userId).enqueue(new Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    UserStorage.getInstance().setBlockNote("");
                    return;
                }
                try {
                    String note = response.body().string();
                    UserStorage.getInstance().setBlockNote(note == null ? "" : note);
                } catch (Exception e) {
                    UserStorage.getInstance().setBlockNote("");
                }
            }

            @Override
            public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
                UserStorage.getInstance().setBlockNote("");
            }
        });
    }
}
