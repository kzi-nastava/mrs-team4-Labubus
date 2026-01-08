package com.ubre.backend.dto;

// For sending request via email to join a ride

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PassengerRequestDto {
    private Long rideId;
    private String email;

    public PassengerRequestDto() {
    }
    public PassengerRequestDto(Long rideId, String email) {
        this.rideId = rideId;
        this.email = email;
    }
}
