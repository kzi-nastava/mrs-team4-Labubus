package com.ubre.backend.dto;

// this comes from frontend only, no usage later

import com.ubre.backend.enums.VehicleType;
import com.ubre.backend.model.Waypoint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RideOrderDto {
    private Long id;
    private Long creatorId;
    private List<WaypointDto> waypoints;
    public VehicleType vehicleType;
    public Boolean babyFriendly;
    public Boolean petFriendly;
    public LocalDateTime scheduledTime; // null if immediate ride
    public Double distance; // in meters
    public Double requiredTime; // in seconds

    public RideOrderDto(Long id, Long creatorId, List<WaypointDto> waypoints, VehicleType vehicleType, Boolean babyFriendly, Boolean petFriendly, LocalDateTime scheduledTime, Double distance, Double requiredTime) {
        this.id = id;
        this.creatorId = creatorId;
        this.waypoints = waypoints;
        this.vehicleType = vehicleType;
        this.babyFriendly = babyFriendly;
        this.petFriendly = petFriendly;
        this.scheduledTime = scheduledTime;
        this.distance = distance;
        this.requiredTime = requiredTime;
    }
}
