package com.ubre.backend.dto;

// Ride progress updates, including current location and estimated time of arrival to destination

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RideProgressDto {
    private Long rideId;
    private WaypointDto location;
    private Integer estimatedTime;

    public RideProgressDto(Long rideId, WaypointDto location, int estimatedTime) {
        this.rideId = rideId;
        this.location = location;
        this.estimatedTime = estimatedTime;
    }
}
