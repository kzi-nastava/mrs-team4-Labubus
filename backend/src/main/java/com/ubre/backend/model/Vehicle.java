package com.ubre.backend.model;

import com.ubre.backend.dto.VehicleDto;
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

    @Column(name = "plates", nullable = false, unique = true)
    private String plates;

    @Column(name = "baby_friendly")
    private Boolean babyFriendly = false;

    @Column(name = "pet_friendly")
    private Boolean petFriendly = false;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location")
    private Waypoint location;

    // Constructors
    public Vehicle() {}

    public Vehicle(String model, VehicleType type, Integer seats, String plates, Boolean babyFriendly, Boolean petFriendly) {
        this.model = model;
        this.type = type;
        this.seats = seats;
        this.plates = plates;
        this.babyFriendly = babyFriendly;
        this.petFriendly = petFriendly;
    }

    public Vehicle(VehicleDto dto) {
        this.id = dto.getId();
        this.model = dto.getModel();
        this.type = dto.getType();
        this.seats = dto.getSeats();
        this.babyFriendly = dto.getBabyFriendly();
        this.petFriendly = dto.getPetFriendly();
        this.plates = dto.getPlates();
    }
}
