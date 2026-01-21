package com.ubre.backend.websocket;

import com.ubre.backend.dto.RideDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class RideAssignmentNotification {
    private final String status;
    private final RideDto ride;

    public RideAssignmentNotification(String status, RideDto ride) {
        this.status = status;
        this.ride = ride;
    }
}
