package com.ubre.backend.dto;

// Waypoint Data Transfer Object
// Used to represent a geographical waypoint with an ID, label, latitude, and longitude

import com.ubre.backend.model.Waypoint;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class WaypointDto implements Serializable {
    private Long id;
    private String label;
    @NotNull(message = "Latitude cannot be null")
    private Double latitude;
    @NotNull(message = "Longitude cannot be null")
    private Double longitude;
    private Boolean visited;

    public WaypointDto(Long id, String label, double latitude, double longitude) {
        this.id = id;
        this.label = label;
        this.latitude = latitude;
        this.longitude = longitude;
        this.visited = false;
    }

    public WaypointDto(Long id, String label, double latitude, double longitude, Boolean visited) {
        this.id = id;
        this.label = label;
        this.latitude = latitude;
        this.longitude = longitude;
        this.visited = visited;
    }

    public WaypointDto(Waypoint model) {
        this.id = model.getId();
        this.label = model.getLabel();
        this.latitude = model.getLatitude();
        this.longitude = model.getLongitude();
        this.visited = model.getVisited();
    }
}
