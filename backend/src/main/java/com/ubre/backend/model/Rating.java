package com.ubre.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ratings")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_rating", nullable = false)
    private int vehicleRating;

    @Column(name = "driver_rating", nullable = false)
    private int driverRating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @OneToOne
    @JoinColumn(name = "ride_id", nullable = false)
    private Ride ride;

    @ManyToOne
    @JoinColumn(name = "rater_id", nullable = false)
    private RegisteredUser rater;

    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    // Constructors
    public Rating() {
        this.createdAt = LocalDateTime.now();
        this.expiryDate = LocalDateTime.now().plusDays(3);
    }

    public Rating(int vehicleRating, int driverRating, Ride ride, RegisteredUser rater, Driver driver) {
        this.vehicleRating = vehicleRating;
        this.driverRating = driverRating;
        this.ride = ride;
        this.rater = rater;
        this.driver = driver;
        this.createdAt = LocalDateTime.now();
        this.expiryDate = LocalDateTime.now().plusDays(3);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVehicleRating() {
        return vehicleRating;
    }

    public void setVehicleRating(int vehicleRating) {
        this.vehicleRating = vehicleRating;
    }

    public int getDriverRating() {
        return driverRating;
    }

    public void setDriverRating(int driverRating) {
        this.driverRating = driverRating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Ride getRide() {
        return ride;
    }

    public void setRide(Ride ride) {
        this.ride = ride;
    }

    public RegisteredUser getRater() {
        return rater;
    }

    public void setRater(RegisteredUser rater) {
        this.rater = rater;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }
}
