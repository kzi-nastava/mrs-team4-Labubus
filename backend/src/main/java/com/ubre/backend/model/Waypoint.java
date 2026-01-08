package com.ubre.backend.model;

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

    // Constructors
    public Waypoint() {}

    public Waypoint(String label, Double latitude, Double longitude) {
        this.label = label;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
