package com.example.ubre.ui.notifications;

import com.example.ubre.ui.dtos.RideDto;
import com.example.ubre.ui.enums.NotificationType;

import java.io.Serializable;

//import { NotificationType } from "../enums/notification-type";
//import { RideDto } from "../dtos/ride-dto";
//
//export interface CurrentRideNotification {
//    status: NotificationType;
//    ride: RideDto | null;
//    reason: string | null;
//}

public class CurrentRideNotification implements Serializable {
    private NotificationType status;
    private RideDto ride; // may be null
    private String reason; // may be null

    public CurrentRideNotification() {
    }

    public CurrentRideNotification(NotificationType status, RideDto ride, String reason) {
        this.status = status;
        this.ride = ride;
        this.reason = reason;
    }

    public CurrentRideNotification(NotificationType status, RideDto ride) {
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "CurrentRideNotification{" +
                "status=" + status +
                ", ride=" + ride +
                ", reason='" + reason + '\'' +
                '}';
    }
}
