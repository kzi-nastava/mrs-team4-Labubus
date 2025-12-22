package com.example.ubre.ui.model;

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
}
