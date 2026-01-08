package com.ubre.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

// Ride history, active rides, my favourites

@Getter
@Setter
public class RideCardDto {
    private Long id;
    private LocalDateTime startTime;
    public Collection<WaypointDto> waypoints;
    public Boolean favorite;

    public RideCardDto(Long id, LocalDateTime startTime, ArrayList<WaypointDto> waypoints, Boolean favorite) {
        this.id = id;
        this.startTime = startTime;
        this.waypoints = waypoints;
        this.favorite = favorite;
    }
}
