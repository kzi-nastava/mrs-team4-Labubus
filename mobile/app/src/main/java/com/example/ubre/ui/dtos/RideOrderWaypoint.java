package com.example.ubre.ui.dtos;

public class RideOrderWaypoint {
    public Long id;
    public String label;
    public Double latitude;
    public Double longitude;
    public boolean visited;

    public RideOrderWaypoint(Long id, String label, Double latitude, Double longitude, boolean visited) {
        this.id = id;
        this.label = label;
        this.latitude = latitude;
        this.longitude = longitude;
        this.visited = visited;
    }
}
