package com.ubre.backend.dto;

import com.ubre.backend.enums.UserStatus;
import lombok.Getter;
import lombok.Setter;

// Every vehicle that is visible on the map has some indicator info

@Getter
@Setter
public class VehicleIndicatorDto {
    private Long driverId;
    private WaypointDto location;
    private UserStatus status;
    private Boolean panic;

    public VehicleIndicatorDto() {
    }

    public VehicleIndicatorDto(Long driverId, WaypointDto location, UserStatus status, boolean panic) {
        this.driverId = driverId;
        this.location = location;
        this.status = status;
        this.panic = panic;
    }
}
