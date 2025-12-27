package com.ubre.backend.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

// Ride history, active rides, my favourites

public class RideCardDto {
    private int id;
    private LocalDateTime startTime;
    public ArrayList<WaypointDto> waypoints;

    public RideCardDto(int id, LocalDateTime startTime, ArrayList<WaypointDto> waypoints) {
        this.id = id;
        this.startTime = startTime;
        this.waypoints = waypoints;
    }

    public int getId() {
        return id;
    }
    public LocalDateTime getStartTime() {
        return startTime;
    }
    public ArrayList<WaypointDto> getWaypoints() {
        return waypoints;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    public void setWaypoints(ArrayList<WaypointDto> waypoints) {
        this.waypoints = waypoints;
    }
}
