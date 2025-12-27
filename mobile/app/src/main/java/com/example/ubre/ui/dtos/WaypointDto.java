package com.example.ubre.ui.dtos;

// Waypoint Data Transfer Object
// Used to represent a geographical waypoint with an ID, label, latitude, and longitude

public class WaypointDto {
    private String id;
    private String label;
    private double latitude;
    private double longitude;
    public WaypointDto(String id, String label, double latitude, double longitude) {
        this.id = id;
        this.label = label;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
