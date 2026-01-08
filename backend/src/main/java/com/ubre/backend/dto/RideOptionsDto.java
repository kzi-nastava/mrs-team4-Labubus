package com.ubre.backend.dto;

import com.ubre.backend.enums.VehicleType;
import lombok.Getter;
import lombok.Setter;

// When ordering a ride, this DTO carries the selected ride options.

@Getter
@Setter
public class RideOptionsDto {
    private VehicleType vehicleType;
    private Boolean babyFriendly;
    private Boolean petFriendly;
    public RideOptionsDto(VehicleType vehicleType, boolean babyFriendly, boolean petFriendly) {
        this.vehicleType = vehicleType;
        this.babyFriendly = babyFriendly;
        this.petFriendly = petFriendly;
    }
}
