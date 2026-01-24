package com.ubre.backend.websocket;

import com.ubre.backend.dto.RideDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CurrentRideNotification {
    public String status;
    public RideDto ride;

    public CurrentRideNotification(String status, RideDto ride) {
        this.status = status;
        this.ride = ride;
    }
}
