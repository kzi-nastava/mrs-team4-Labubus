package com.example.ubre.ui.storages;

// main difference between this service, and angular serices, is that there is no logic here, only state management, and api calls with repositories is separate class.
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ubre.ui.dtos.UserDto;

public class UserStorage {

    private static UserStorage instance;
    private MutableLiveData<UserDto> currentUser = new MutableLiveData<>(null);
    private MutableLiveData<byte[]> currentUserAvatar = new MutableLiveData<>(null);

    private UserStorage() {
    }

    public static synchronized UserStorage getInstance() {
        if (instance == null) {
            instance = new UserStorage();
        }
        return instance;
    }

    public MutableLiveData<UserDto> getCurrentUser() {
        return currentUser;
    }

    public LiveData<UserDto> getCurrentUserReadOnly() {
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

    public MutableLiveData<byte[]> getCurrentUserAvatar() {
        return currentUserAvatar;
    }

    public LiveData<byte[]> getCurrentUserAvatarReadOnly() {
        return currentUserAvatar;
    }

    public void setCurrentUserAvatar(byte[] avatar) {
        currentUserAvatar.setValue(avatar);
    }

    public void clearCurrentUserAvatar() {
        currentUserAvatar.setValue(null);
    }
}
