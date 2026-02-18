package com.ubre.backend.model;

import com.ubre.backend.dto.WaypointDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "waypoints")
public class Waypoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String label;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column
    private Boolean visited;

    // Constructors
    public Waypoint() {}

    public Waypoint(String label, Double latitude, Double longitude) {
        this.label = label;
        this.latitude = latitude;
        this.longitude = longitude;
        this.visited = false;
    }

    public Waypoint(String label, Double latitude, Double longitude, Boolean visited) {
        this.label = label;
        this.latitude = latitude;
        this.longitude = longitude;
        this.visited = visited;
    }

    public Waypoint(WaypointDto dto) {
        this.id = dto.getId();
        this.label = dto.getLabel();
        this.latitude = dto.getLatitude();
        this.longitude = dto.getLongitude();
        this.visited = dto.getVisited();
    }
}
