package com.ubre.backend.dto;

import com.ubre.backend.enums.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// When ordering a ride, this DTO carries the selected ride options.

@Getter
@Setter
@NoArgsConstructor
public class RideOptionsDto {
    @NotNull(message = "Vehicle type is required")
    private VehicleType vehicleType;
    @NotNull(message = "Baby friendly option is required")
    private Boolean babyFriendly;
    @NotNull(message = "Pet friendly option is required")
    private Boolean petFriendly;

    public RideOptionsDto(VehicleType vehicleType, boolean babyFriendly, boolean petFriendly) {
        this.vehicleType = vehicleType;
        this.babyFriendly = babyFriendly;
        this.petFriendly = petFriendly;
    }
}
