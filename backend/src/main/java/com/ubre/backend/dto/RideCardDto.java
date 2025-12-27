package com.ubre.backend.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

// Ride history, active rides, my favourites

public class RideCardDto {
    private Long id;
    private LocalDateTime startTime;
    public Collection<WaypointDto> waypoints;

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
    public Collection<WaypointDto> getWaypoints() {
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
