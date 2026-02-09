package com.ubre.backend.websocket;

import com.ubre.backend.dto.UserDto;

public class ProfileChangeNotification {
    private final String status;
    private final UserDto user;

    public ProfileChangeNotification(String status, UserDto user) {
        this.status = status;
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public UserDto getUser() {
        return user;
    }
}
