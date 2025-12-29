package com.example.ubre.ui.dtos;

// Waypoint Data Transfer Object
// Used to represent a geographical waypoint with an ID, label, latitude, and longitude

import java.io.Serializable;

public class WaypointDto implements Serializable {
    private Long id;
    private String label;
    private double latitude;
    private double longitude;
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

    public String getLabel() {
        return label;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
