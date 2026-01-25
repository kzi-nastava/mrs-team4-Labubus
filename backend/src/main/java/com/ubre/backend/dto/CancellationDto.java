package com.ubre.backend.dto;

// DTO for ride cancellation requests

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CancellationDto {
    private Long rideId;
    private String reason;

    public CancellationDto(Long rideId, String reason) {
        this.rideId = rideId;
        this.reason = reason;
    }
}
