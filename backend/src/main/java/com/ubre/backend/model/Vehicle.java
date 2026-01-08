package com.ubre.backend.model;

import com.ubre.backend.enums.VehicleType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String model;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private VehicleType type;

    @Column(name = "seats", nullable = false)
    private Integer seats;

    @Column(name = "baby_friendly")
    private Boolean babyFriendly = false;

    @Column(name = "pet_friendly")
    private Boolean petFriendly = false;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location", nullable = false)
    private Waypoint location;

    // Constructors
    public Vehicle() {}

    public Vehicle(String model, VehicleType type, Integer seats, Boolean babyFriendly, Boolean petFriendly) {
        this.model = model;
        this.type = type;
        this.seats = seats;
        this.babyFriendly = babyFriendly;
        this.petFriendly = petFriendly;
    }
}
