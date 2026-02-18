package com.ubre.backend.dto;

// this comes from frontend only, no usage later

import com.ubre.backend.enums.VehicleType;
import com.ubre.backend.model.Waypoint;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "Creator ID cannot be null")
    private Long creatorId;
    private List<String> passengersEmails;
    @Valid
    @NotEmpty(message = "Waypoints cannot be empty")
    private List<WaypointDto> waypoints;
    @NotNull(message = "Vehicle type cannot be null")
    public VehicleType vehicleType;
    public Boolean babyFriendly;
    public Boolean petFriendly;
    public String scheduledTime; // null if immediate ride
    @NotNull(message = "Distance cannot be null")
    public Double distance; // in meters
    @NotNull(message = "Required time cannot be null")
    public Double requiredTime; // in seconds
    @NotNull(message = "Price cannot be null")
    public Double price;

    public RideOrderDto(Long id, Long creatorId, List<String> passengersEmails, List<WaypointDto> waypoints, VehicleType vehicleType, Boolean babyFriendly, Boolean petFriendly, String scheduledTime, Double distance, Double requiredTime, Double price) {
        this.id = id;
        this.creatorId = creatorId;
        this.passengersEmails = passengersEmails;
        this.waypoints = waypoints;
        this.vehicleType = vehicleType;
        this.babyFriendly = babyFriendly;
        this.petFriendly = petFriendly;
        this.scheduledTime = scheduledTime;
        this.distance = distance;
        this.requiredTime = requiredTime;
        this.price = price;
    }
}
