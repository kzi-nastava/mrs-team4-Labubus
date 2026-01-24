package com.ubre.backend.dto;

import com.ubre.backend.enums.RideStatus;
import com.ubre.backend.model.Ride;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RideDto implements Serializable {
    private Long id;
    @NotNull(message = "Start time cannot be null")
    private String startTime; // ISO 8601 format
    private String endTime;
    @NotEmpty(message = "Waypoints cannot be empty")
    @Size(min = 2)
    private List<WaypointDto> waypoints;
    @NotNull(message = "Driver cannot be null")
    private UserDto driver;
    private List<UserDto> passengers;
    private Boolean panic;
    private Long canceledBy;
    private Double price;
    @NotNull(message = "Distance cannot be null")
    private Double distance;
    @NotNull(message = "Ride status cannot be null")
    private RideStatus status;
    @NotNull(message = "Creator ID cannot be null")
    private Long createdBy;

    public RideDto(Long id, String startTime, String endTime, List<WaypointDto> waypoints, UserDto driver, List<UserDto> passengers, Boolean panic, Long canceledBy, Double price, Double distance, RideStatus status, Long createdBy) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.waypoints = waypoints;
        this.driver = driver;
        this.passengers = passengers;
        this.panic = panic;
        this.canceledBy = canceledBy;
        this.price = price;
        this.distance = distance;
        this.status = status;
        this.createdBy = createdBy;
    }

    public RideDto(Ride model) {
        this.id = model.getId();
        this.startTime = model.getStartTime().toString();
        this.endTime = model.getEndTime().toString();
        this.waypoints = model.getWaypoints().stream().map(WaypointDto::new).toList();
        this.driver = new UserDto(model.getDriver());
        this.passengers = model.getPassengers().stream().map(UserDto::new).toList();
        this.panic = model.getPanic();
        this.canceledBy = model.getCanceledBy() != null ? model.getCanceledBy().getId() : null;
        this.price = model.getPrice();
        this.distance = model.getDistance();
        this.status = model.getStatus();
        this.createdBy = model.getCreator() == null ? null : model.getCreator().getId();
    }
}