package com.example.ubre.ui.dtos;

import java.io.Serializable;
import java.time.LocalDateTime;

public class RideDto implements Serializable {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private WaypointDto[] waypoints;
    private UserDto driver;
    private UserDto[] passengers;
    private Boolean panic;
    private String canceledBy;
    private VehicleDto vehicle;
    private Double price;
    private Double distance;

    public RideDto(Long id, LocalDateTime start, LocalDateTime end, WaypointDto[] waypoints, UserDto driver, UserDto[] passengers, Boolean panic, String canceledBy, VehicleDto vehicle, Double price, Double distance) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.waypoints = waypoints;
        this.driver = driver;
        this.passengers = passengers;
        this.panic = panic;
        this.canceledBy = canceledBy;
        this.vehicle = vehicle;
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

    public UserDto[] getPassengers() {
        return passengers;
    }

    public Boolean getPanic() {
        return panic;
    }

    public String getCanceledBy() {
        return canceledBy;
    }

    public VehicleDto getVehicle() {
        return vehicle;
    }

    public Double getPrice() {
        return price;
    }

    public Double getDistance() {
        return distance;
    }
}