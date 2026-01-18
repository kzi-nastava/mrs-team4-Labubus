package com.ubre.backend.dto;

import com.ubre.backend.model.Ride;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

// Ride history, active rides, my favourites

@Getter
@Setter
@NoArgsConstructor
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

    public RideCardDto(Ride model) {
        this.id = model.getId();
        this.startTime = model.getStartTime();
        this.waypoints = model.getWaypoints().stream().map(WaypointDto::new).toList();
        this.favorite = model.getFavorite();
    }
}
