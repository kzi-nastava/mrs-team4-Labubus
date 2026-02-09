package com.ubre.backend.dto;

// For sending request via email to join a ride

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PassengerRequestDto {
    private Long rideId;
    @NotBlank(message = "Email cannot be blank")
    private String email;

    public PassengerRequestDto(Long rideId, String email) {
        this.rideId = rideId;
        this.email = email;
    }
}
