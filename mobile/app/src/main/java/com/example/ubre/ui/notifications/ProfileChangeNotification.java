package com.example.ubre.ui.notifications;

import com.example.ubre.ui.dtos.UserDto;
import com.example.ubre.ui.enums.NotificationType;

import java.io.Serializable;

// TypeScript interface reference:
// import { NotificationType } from '../enums/notification-type';
// import { UserDto } from '../dtos/user-dto';
//
// export interface ProfileChangeNotification {
//     status: NotificationType;
//     user: UserDto | null;
// }

public class ProfileChangeNotification implements Serializable {
    private NotificationType status;
    private UserDto user; // may be null

    public ProfileChangeNotification() {
    }

    public ProfileChangeNotification(NotificationType status, UserDto user) {
        this.status = status;
        this.user = user;
    }

    public NotificationType getStatus() {
        return status;
    }

    public void setStatus(NotificationType status) {
        this.status = status;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "ProfileChangeNotification{" +
                "status=" + status +
                ", user=" + user +
                '}';
    }
}
