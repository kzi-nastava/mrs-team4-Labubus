package com.example.ubre.ui.notifications;

import com.example.ubre.ui.dtos.RideDto;
import com.example.ubre.ui.enums.NotificationType;

import java.io.Serializable;

public class RideAssignmentNotification implements Serializable {
    private NotificationType status;
    private RideDto ride; // may be null

    public RideAssignmentNotification() {
    }

    public RideAssignmentNotification(NotificationType status, RideDto ride) {
        this.status = status;
        this.ride = ride;
    }

    public NotificationType getStatus() {
        return status;
    }

    public void setStatus(NotificationType status) {
        this.status = status;
    }

    public RideDto getRide() {
        return ride;
    }

    public void setRide(RideDto ride) {
        this.ride = ride;
    }

    @Override
    public String toString() {
        return "RideAssignmentNotification{" +
                "status=" + status +
                ", ride=" + ride +
                '}';
    }
}

