package com.example.ubre.ui.services;

// this service represents currently logged in user and provides methods to access user data and perform user-related operations// java

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ubre.ui.dtos.UserDto;

public class UserService {

    private static UserService instance;
    private final MutableLiveData<UserDto> currentUser = new MutableLiveData<>();

    private UserService() {
    }

    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
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


    // api calls to backend for user-related operations would go here


}
