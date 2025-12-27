package com.example.ubre.ui.dtos;

import java.io.Serializable;
import java.time.LocalDateTime;

public class RideDto implements Serializable {
    private int id;
    private LocalDateTime start;
    private LocalDateTime end;
    private WaypointDto[] waypoints;
    private UserDto driver;
    private UserDto[] passengers;
    private boolean panic;
    private String canceledBy;
    private double price;
    private double distance;

    public RideDto(int id, LocalDateTime start, LocalDateTime end, WaypointDto[] waypoints, UserDto driver, UserDto[] passengers, boolean panic, String canceledBy, double price, double distance) {
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

    public int getId() {
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

    public boolean isPanic() {
        return panic;
    }

    public String getCanceledBy() {
        return canceledBy;
    }

    public double getPrice() {
        return price;
    }

    public double getDistance() {
        return distance;
    }
}