package com.ubre.backend.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;

public class RideDto implements Serializable {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private WaypointDto[] waypoints;
    private UserDto driver;
    private Collection<UserDto> passengers;
    private Boolean panic;
    private String canceledBy;
    private Double price;
    private Double distance;

    public RideDto() {
    }

    public RideDto(Long id, LocalDateTime start, LocalDateTime end, WaypointDto[] waypoints, UserDto driver, Collection<UserDto> passengers, boolean panic, String canceledBy, double price, double distance) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.waypoints = waypoints;
        this.driver = driver;
        this.passengers = passengers;
        this.panic = panic;
        this.canceledBy = canceledBy;
        this.price = price;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public WaypointDto[] getWaypoints() {
        return waypoints;
    }

    public UserDto getDriver() {
        return driver;
    }

    public Collection<UserDto> getPassengers() {
        return passengers;
    }

    public Boolean isPanic() {
        return panic;
    }

    public String getCanceledBy() {
        return canceledBy;
    }

    public Double getPrice() {
        return price;
    }

    public Double getDistance() {
        return distance;
    }
}