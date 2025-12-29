package com.example.ubre.ui.dtos;

import java.time.LocalDateTime;
import java.util.ArrayList;

// Ride history, active rides, my favourites

public class RideCardDto {
    private Long id;
    private LocalDateTime startTime;
    public ArrayList<WaypointDto> waypoints;

    public RideCardDto(Long id, LocalDateTime startTime, ArrayList<WaypointDto> waypoints) {
        this.id = id;
        this.startTime = startTime;
        this.waypoints = waypoints;
    }

    public Long getId() {
        return id;
    }
    public LocalDateTime getStartTime() {
        return startTime;
    }
    public ArrayList<WaypointDto> getWaypoints() {
        return waypoints;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    public void setWaypoints(ArrayList<WaypointDto> waypoints) {
        this.waypoints = waypoints;
    }
}
