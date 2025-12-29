package com.ubre.backend.dto;

import java.util.ArrayList;
import java.util.Collection;

// By providing ride estimations when or before ordering (guest can see it too)

public class RideEstimationsDto {
    private Collection<WaypointDto> waypoints;
    private Double price;
    private Integer duration; // in minutes

    public RideEstimationsDto() {
    }
    public RideEstimationsDto(ArrayList<WaypointDto> waypoints, double price, int duration) {
        this.waypoints = waypoints;
        this.price = price;
        this.duration = duration;
    }
    public Collection<WaypointDto> getWaypoints() {
        return waypoints;
    }
    public void setWaypoints(ArrayList<WaypointDto> waypoints) {
        this.waypoints = waypoints;
    }
    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }
    public Integer getDuration() {
        return duration;
    }
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}
