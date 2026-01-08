package com.ubre.backend.dto;

// DTO for ride cancellation requests

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancellationDto {
    private Long rideId;
    private String reason;

    public CancellationDto() {
    }

    public CancellationDto(Long rideId, String reason) {
        this.rideId = rideId;
        this.reason = reason;
    }
}
