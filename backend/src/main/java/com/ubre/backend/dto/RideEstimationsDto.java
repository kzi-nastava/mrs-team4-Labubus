package com.ubre.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// By providing ride estimations when or before ordering (guest can see it too)

@Getter
@Setter
@NoArgsConstructor
public class RideEstimationsDto {
    @NotEmpty
    @Size(min = 2)
    private List<WaypointDto> waypoints;
    private Double price;
    private Integer duration; // in minutes

    public RideEstimationsDto(ArrayList<WaypointDto> waypoints, double price, int duration) {
        this.waypoints = waypoints;
        this.price = price;
        this.duration = duration;
    }
}
