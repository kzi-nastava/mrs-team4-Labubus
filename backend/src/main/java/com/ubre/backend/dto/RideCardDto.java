package com.ubre.backend.dto;

import com.ubre.backend.model.Ride;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// Ride history, active rides, my favourites

@Getter
@Setter
@NoArgsConstructor
public class RideCardDto {
    @NotNull(message = "Ride ID is required")
    private Long id;
    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;
    @NotEmpty(message = "Waypoints cannot be empty")
    @Size(min = 2)
    public List<WaypointDto> waypoints;
    @NotNull(message = "Favorite status is required")
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
