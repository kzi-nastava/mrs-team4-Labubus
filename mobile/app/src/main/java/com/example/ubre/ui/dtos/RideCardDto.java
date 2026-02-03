package com.example.ubre.ui.dtos;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Ride history, active rides, my favourites

public class RideCardDto {
    private Long id;
    private LocalDateTime startTime;
    public List<WaypointDto> waypoints;
    public Boolean favorite;

    public RideCardDto(Long id, LocalDateTime startTime, List<WaypointDto> waypoints, Boolean favorite) {
        this.id = id;
        this.startTime = startTime;
        this.waypoints = waypoints;
        this.favorite = favorite;
    }

    public Long getId() {
        return id;
    }
    public LocalDateTime getStartTime() {
        return startTime;
    }
    public List<WaypointDto> getWaypoints() {
        return waypoints;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    public void setWaypoints(List<WaypointDto> waypoints) {
        this.waypoints = waypoints;
    }
}
