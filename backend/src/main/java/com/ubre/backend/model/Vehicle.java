package com.ubre.backend.model;

import com.ubre.backend.enums.VehicleType;
import jakarta.persistence.*;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String model;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false)
    private VehicleType vehicleType;

    @Column(name = "license_plate", nullable = false, unique = true)
    private String licensePlate;

    @Column(name = "number_of_seats", nullable = false)
    private int numberOfSeats;

    @Column(name = "allows_babies")
    private boolean allowsBabies = false;

    @Column(name = "allows_pets")
    private boolean allowsPets = false;

    @Column(name = "is_active")
    private boolean isActive = true;

    @OneToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    // Constructors
    public Vehicle() {}

    public Vehicle(String model, VehicleType vehicleType, String licensePlate, int numberOfSeats) {
        this.model = model;
        this.vehicleType = vehicleType;
        this.licensePlate = licensePlate;
        this.numberOfSeats = numberOfSeats;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public boolean isAllowsBabies() {
        return allowsBabies;
    }

    public void setAllowsBabies(boolean allowsBabies) {
        this.allowsBabies = allowsBabies;
    }

    public boolean isAllowsPets() {
        return allowsPets;
    }

    public void setAllowsPets(boolean allowsPets) {
        this.allowsPets = allowsPets;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }
}
