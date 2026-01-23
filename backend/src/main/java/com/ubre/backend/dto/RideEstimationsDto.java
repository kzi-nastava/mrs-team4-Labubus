package com.ubre.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// By providing ride estimations when or before ordering (guest can see it too)

@Getter
@Setter
public class RideEstimationsDto {
    private List<WaypointDto> waypoints;
    private Double price;
    private Integer duration; // in minutes

    public RideEstimationsDto() {
    }
    public RideEstimationsDto(ArrayList<WaypointDto> waypoints, double price, int duration) {
        this.waypoints = waypoints;
        this.price = price;
        this.duration = duration;
    }
}
