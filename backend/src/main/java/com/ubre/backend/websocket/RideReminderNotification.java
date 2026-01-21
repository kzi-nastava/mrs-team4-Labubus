package com.ubre.backend.websocket;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RideReminderNotification {
    public String status;
    public String time;

    public RideReminderNotification(String status, String time) { // time is in format "HH:MM"
        this.status = status;
        this.time = time;
    }
}
