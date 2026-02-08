package com.example.ubre.ui.dtos;

import java.util.ArrayList;
import java.util.List;

// By providing ride estimations when or before ordering (guest can see it too)

public class RideEstimationsDto {
    private List<WaypointDto> waypoints;
    private Double price;
    private Integer duration; // in minutes

    public RideEstimationsDto() {
    }
    public RideEstimationsDto(List<WaypointDto> waypoints, Double price, Integer duration) {
        this.waypoints = waypoints;
        this.price = price;
        this.duration = duration;
    }
    public List<WaypointDto> getWaypoints() {
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
