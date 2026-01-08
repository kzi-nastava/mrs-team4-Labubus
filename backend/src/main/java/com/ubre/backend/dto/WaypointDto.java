package com.ubre.backend.dto;

// Waypoint Data Transfer Object
// Used to represent a geographical waypoint with an ID, label, latitude, and longitude

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class WaypointDto implements Serializable {
    private Long id;
    private String label;
    private Double latitude;
    private Double longitude;
    public WaypointDto(Long id, String label, double latitude, double longitude) {
        this.id = id;
        this.label = label;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
