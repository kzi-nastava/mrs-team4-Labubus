package com.example.ubre.ui.dtos;

import com.example.ubre.ui.enums.RideStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class RideDto implements Serializable {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<WaypointDto> waypoints;
    private UserDto driver;
    private List<UserDto> passengers;
    private Boolean panic;
    private Long canceledBy;
    private Double price;
    private Double distance;
    private RideStatus status;
    private Long createdBy;

    public RideDto(Long id, LocalDateTime startTime, LocalDateTime endTime, List<WaypointDto> waypoints, UserDto driver, List<UserDto> passengers, Boolean panic, Long canceledBy, Double price, Double distance, RideStatus status, Long createdBy) {
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

    public Long getId() {
        return id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public List<WaypointDto> getWaypoints() {
        return waypoints;
    }

    public UserDto getDriver() {
        return driver;
    }

    public List<UserDto> getPassengers() {
        return passengers;
    }

    public Boolean getPanic() {
        return panic;
    }

    public Long getCanceledBy() {
        return canceledBy;
    }

    public Double getPrice() {
        return price;
    }

    public Double getDistance() {
        return distance;
    }
    public RideStatus getStatus() {
        return status;
    }
    public Long getCreatedBy() {
        return createdBy;
    }
}