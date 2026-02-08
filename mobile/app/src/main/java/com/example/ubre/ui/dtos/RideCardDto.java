package com.example.ubre.ui.dtos;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// Ride history, active rides, my favourites

public class RideCardDto implements Serializable {
    private Long id;
    private String startTime;
    public List<WaypointDto> waypoints;
    public Boolean favorite;

    public RideCardDto(Long id, String startTime, List<WaypointDto> waypoints, Boolean favorite) {
        this.id = id;
        this.startTime = startTime;
        this.waypoints = waypoints;
        this.favorite = favorite;
    }

    public Long getId() {
        return id;
    }
    public LocalDateTime getStartTime() {return LocalDateTime.parse(startTime);}
    public List<WaypointDto> getWaypoints() {
        return waypoints;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public void setStartTime(LocalDateTime startTime) {this.startTime = startTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);}
    public void setWaypoints(List<WaypointDto> waypoints) {
        this.waypoints = waypoints;
    }
}
