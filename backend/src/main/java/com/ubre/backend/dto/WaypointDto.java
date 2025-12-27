package com.ubre.backend.dto;

// Waypoint Data Transfer Object
// Used to represent a geographical waypoint with an ID, label, latitude, and longitude

public class WaypointDto {
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
