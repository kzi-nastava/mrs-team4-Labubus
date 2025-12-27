package com.example.ubre.ui.dtos;

import java.util.ArrayList;

// By providing ride estimations when or before ordering (guest can see it too)

public class RideEstimationsDto {
    private ArrayList<WaypointDto> waypoints;
    private double price;
    private int duration; // in minutes

    public RideEstimationsDto() {
    }
    public RideEstimationsDto(ArrayList<WaypointDto> waypoints, double price, int duration) {
        this.waypoints = waypoints;
        this.price = price;
        this.duration = duration;
    }
    public ArrayList<WaypointDto> getWaypoints() {
        return waypoints;
    }
    public void setWaypoints(ArrayList<WaypointDto> waypoints) {
        this.waypoints = waypoints;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
}
