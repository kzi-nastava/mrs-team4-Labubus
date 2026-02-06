package com.example.ubre.ui.repositories;

import com.example.ubre.ui.dtos.UserDto;

public class UserRepository {
    private static final String TAG = "UserRepository";
    private static UserRepository instance;

    private UserRepository() {
    }

    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    public UserDto getUserProfile() { // after login, we fetch user profile data to display in profile screen and use in other parts of the app

    }


}
