package com.ubre.backend.model;

import com.ubre.backend.enums.RideStatus;
import com.ubre.backend.enums.VehicleType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rides")
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "start_location_id", nullable = false)
    private Location startLocation;

    @ManyToOne
    @JoinColumn(name = "end_location_id", nullable = false)
    private Location endLocation;

    @Column(name = "scheduled_time")
    private LocalDateTime scheduledTime;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "estimated_duration")
    private int estimatedDuration; // in minutes

    @Column(name = "actual_duration")
    private int actualDuration; // in minutes

    @Column(name = "estimated_distance")
    private double estimatedDistance; // in kilometers

    @Column(name = "actual_distance")
    private double actualDistance; // in kilometers

    @Column(name = "total_price")
    private double totalPrice;

    @Column(name = "price_per_km")
    private double pricePerKm;

    @Column(name = "base_price")
    private double basePrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "ride_status", nullable = false)
    private RideStatus rideStatus = RideStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false)
    private VehicleType vehicleType;

    @Column(name = "allows_babies")
    private boolean allowsBabies = false;

    @Column(name = "allows_pets")
    private boolean allowsPets = false;

    @Column(name = "cancellation_reason")
    private String cancellationReason;

    @Column(name = "is_panic_activated")
    private boolean isPanicActivated = false;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private RegisteredUser creator;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @ManyToMany
    @JoinTable(
        name = "ride_passengers",
        joinColumns = @JoinColumn(name = "ride_id"),
        inverseJoinColumns = @JoinColumn(name = "passenger_id")
    )
    private List<RegisteredUser> passengers = new ArrayList<>();

    @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<Waypoint> waypoints = new ArrayList<>();

    @OneToOne(mappedBy = "ride", cascade = CascadeType.ALL)
    private Rating rating;

    @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL)
    private List<Notification> notifications = new ArrayList<>();

    @OneToOne(mappedBy = "ride", cascade = CascadeType.ALL)
    private PanicAlert panicAlert;

    @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL)
    private List<RideInconsistencyReport> inconsistencyReports = new ArrayList<>();

    // Constructors
    public Ride() {}

    public Ride(Location startLocation, Location endLocation, RegisteredUser creator, VehicleType vehicleType) {
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.creator = creator;
        this.vehicleType = vehicleType;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    public Location getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(Location endLocation) {
        this.endLocation = endLocation;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public int getEstimatedDuration() {
        return estimatedDuration;
    }

    public void setEstimatedDuration(int estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }

    public int getActualDuration() {
        return actualDuration;
    }

    public void setActualDuration(int actualDuration) {
        this.actualDuration = actualDuration;
    }

    public double getEstimatedDistance() {
        return estimatedDistance;
    }

    public void setEstimatedDistance(double estimatedDistance) {
        this.estimatedDistance = estimatedDistance;
    }

    public double getActualDistance() {
        return actualDistance;
    }

    public void setActualDistance(double actualDistance) {
        this.actualDistance = actualDistance;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getPricePerKm() {
        return pricePerKm;
    }

    public void setPricePerKm(double pricePerKm) {
        this.pricePerKm = pricePerKm;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public RideStatus getRideStatus() {
        return rideStatus;
    }

    public void setRideStatus(RideStatus rideStatus) {
        this.rideStatus = rideStatus;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
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

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public boolean isPanicActivated() {
        return isPanicActivated;
    }

    public void setPanicActivated(boolean panicActivated) {
        isPanicActivated = panicActivated;
    }

    public RegisteredUser getCreator() {
        return creator;
    }

    public void setCreator(RegisteredUser creator) {
        this.creator = creator;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public List<RegisteredUser> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<RegisteredUser> passengers) {
        this.passengers = passengers;
    }

    public List<Waypoint> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(List<Waypoint> waypoints) {
        this.waypoints = waypoints;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public PanicAlert getPanicAlert() {
        return panicAlert;
    }

    public void setPanicAlert(PanicAlert panicAlert) {
        this.panicAlert = panicAlert;
    }

    public List<RideInconsistencyReport> getInconsistencyReports() {
        return inconsistencyReports;
    }

    public void setInconsistencyReports(List<RideInconsistencyReport> inconsistencyReports) {
        this.inconsistencyReports = inconsistencyReports;
    }
}
