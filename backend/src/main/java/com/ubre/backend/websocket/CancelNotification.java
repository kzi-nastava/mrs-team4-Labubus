package com.ubre.backend.websocket;

import com.ubre.backend.dto.RideDto;
import com.ubre.backend.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CancelNotification {
    private String reason;
    public String status;
    public RideDto ride;

    public CancelNotification(String reason, RideDto ride) {
        this.status = NotificationType.RIDE_CANCELLED.name();
        this.reason = reason;
        this.ride = ride;
    }

}
