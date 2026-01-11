package com.ubre.backend.dto;

import com.ubre.backend.enums.UserStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Every vehicle that is visible on the map has some indicator info

@Getter
@Setter
@NoArgsConstructor
public class VehicleIndicatorDto {
    private Long driverId;
    private WaypointDto location;
    private UserStatus status;
    private Boolean panic;

    public VehicleIndicatorDto(Long driverId, WaypointDto location, UserStatus status, Boolean panic) {
        this.driverId = driverId;
        this.location = location;
        this.status = status;
        this.panic = panic;
    }
}
