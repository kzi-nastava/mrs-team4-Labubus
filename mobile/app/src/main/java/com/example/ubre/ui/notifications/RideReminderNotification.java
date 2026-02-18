package com.example.ubre.ui.notifications;

import com.example.ubre.ui.enums.NotificationType;

import java.io.Serializable;

public class RideReminderNotification implements Serializable {
    private NotificationType status;
    private String time; // format HH:MM

    public RideReminderNotification() {
    }

    public RideReminderNotification(NotificationType status, String time) {
        this.status = status;
        this.time = time;
    }

    public NotificationType getStatus() {
        return status;
    }

    public void setStatus(NotificationType status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "RideReminderNotification{" +
                "status=" + status +
                ", time='" + time + '\'' +
                '}';
    }
}

