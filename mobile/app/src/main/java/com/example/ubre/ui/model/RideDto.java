package com.example.ubre.ui.model;

import java.io.Serializable;
import java.time.LocalDate;

public class RideDto implements Serializable {
    private int id;
    private LocalDate start;
    private LocalDate end;
    private String[] waypoints;
    private UserDto driver;
    private UserDto[] passengers;
    private boolean panic;
    private String canceledBy;
    private double price;

    public RideDto(int id, LocalDate start, LocalDate end, String[] waypoints, UserDto driver, UserDto[] passengers, boolean panic, String canceledBy, double price) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.waypoints = waypoints;
        this.driver = driver;
        this.passengers = passengers;
        this.panic = panic;
        this.canceledBy = canceledBy;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public LocalDate getStart() {
        return start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public String[] getWaypoints() {
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
}
