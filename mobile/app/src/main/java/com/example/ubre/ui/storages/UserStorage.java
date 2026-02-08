package com.example.ubre.ui.storages;

// main difference between this service, and angular services, is that there is no logic here, only state management, and api calls with repositories is separate class.
import android.net.Uri;

import androidx.collection.MutableLongIntMap;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ubre.ui.dtos.UserDto;
import com.example.ubre.ui.dtos.UserStatsDto;
import com.example.ubre.ui.dtos.VehicleDto;

public class UserStorage {

    private static UserStorage instance;
    private MutableLiveData<UserDto> currentUser = new MutableLiveData<>(null);
    private MutableLiveData<VehicleDto> currentUserVehicle = new MutableLiveData<>(null); // drivers only, if not a driver, its empty
    private MutableLiveData<byte[]> currentUserAvatar = new MutableLiveData<>(null);
    private MutableLiveData<Uri> pendingAvatarUri = new MutableLiveData<>(null); // for storing the URI of the new avatar before uploading

    // user statistics, only visible to drivers on account settings panel
    private MutableLiveData<UserStatsDto> currentUserStats = new MutableLiveData<>(null);

    private UserStorage() {
    }

    public static synchronized UserStorage getInstance() {
        if (instance == null) {
            instance = new UserStorage();
        }
        return instance;
    }

    public LiveData<UserDto> getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserDto user) {
        currentUser.setValue(user);
    }

    public void updateCurrentUser(UserDto updated) {
        if (updated != null) {
            currentUser.setValue(updated);
        }
    }
    public void clearCurrentUser() {
        currentUser.setValue(null);
    }

    public boolean isLoggedIn() {
        return currentUser.getValue() != null;
    }

    public LiveData<byte[]> getCurrentUserAvatar() {
        return currentUserAvatar;
    }

    public void setCurrentUserAvatar(byte[] avatar) {
        currentUserAvatar.setValue(avatar);
    }

    public void clearCurrentUserAvatar() {
        currentUserAvatar.setValue(null);
    }

    public LiveData<Uri> getPendingAvatarUri() {
        return pendingAvatarUri;
    }

    public void setPendingAvatarUri(Uri uri) {
        pendingAvatarUri.setValue(uri);
    }

    public void clearPendingAvatarUri() {
        pendingAvatarUri.setValue(null);
    }

    public LiveData<VehicleDto> getCurrentUserVehicle() {
        return currentUserVehicle;
    }

    public void setCurrentUserVehicle(VehicleDto vehicle) {
        currentUserVehicle.setValue(vehicle);
    }

    public void clearCurrentUserVehicle() {
        currentUserVehicle.setValue(null);
    }

    public LiveData<UserStatsDto> getCurrentUserStats() {
        return currentUserStats;
    }
    
    public void setCurrentUserStats(UserStatsDto stats) {
        currentUserStats.setValue(stats);
    }

    public void clearCurrentUserStats() {
        currentUserStats.setValue(null);
    }


    // very important method after logout to clear all user related data, and also to be called when app is opened to clear any stale data if exists (if user is not logged in)
    public void clearUserStorage() {
        clearCurrentUser();
        clearCurrentUserAvatar();
        clearPendingAvatarUri();
        clearCurrentUserVehicle();
        clearCurrentUserStats();
    }
}

