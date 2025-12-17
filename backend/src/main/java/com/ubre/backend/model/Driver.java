package com.ubre.backend.model;

import com.ubre.backend.enums.DriverStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("DRIVER")
public class Driver extends User {

    @Column(name = "driving_license")
    private String drivingLicense;

    @Column(name = "active_hours_last_24h")
    private int activeHoursLast24h = 0;

    @Column(name = "is_available")
    private boolean isAvailable = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_status")
    private DriverStatus currentStatus = DriverStatus.INACTIVE;

    @Column(name = "activation_token")
    private String activationToken;

    @Column(name = "activation_token_expiry")
    private LocalDateTime activationTokenExpiry;

    @Column(name = "change_approval_status")
    private String changeApprovalStatus;

    @OneToOne(mappedBy = "driver", cascade = CascadeType.ALL)
    private Vehicle vehicle;

    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL)
    private List<Ride> rides = new ArrayList<>();

    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL)
    private List<Rating> ratingsReceived = new ArrayList<>();

    @OneToMany(mappedBy = "activator", cascade = CascadeType.ALL)
    private List<PanicAlert> panicAlertsActivated = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Chat chat;

    // Constructors
    public Driver() {
        super();
    }

    public Driver(String email, String password, String firstName, String lastName, String address, String phoneNumber) {
        super(email, password, firstName, lastName, address, phoneNumber);
    }

    // Getters and Setters
    public String getDrivingLicense() {
        return drivingLicense;
    }

    public void setDrivingLicense(String drivingLicense) {
        this.drivingLicense = drivingLicense;
    }

    public int getActiveHoursLast24h() {
        return activeHoursLast24h;
    }

    public void setActiveHoursLast24h(int activeHoursLast24h) {
        this.activeHoursLast24h = activeHoursLast24h;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public DriverStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(DriverStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getActivationToken() {
        return activationToken;
    }

    public void setActivationToken(String activationToken) {
        this.activationToken = activationToken;
    }

    public LocalDateTime getActivationTokenExpiry() {
        return activationTokenExpiry;
    }

    public void setActivationTokenExpiry(LocalDateTime activationTokenExpiry) {
        this.activationTokenExpiry = activationTokenExpiry;
    }

    public String getChangeApprovalStatus() {
        return changeApprovalStatus;
    }

    public void setChangeApprovalStatus(String changeApprovalStatus) {
        this.changeApprovalStatus = changeApprovalStatus;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public List<Ride> getRides() {
        return rides;
    }

    public void setRides(List<Ride> rides) {
        this.rides = rides;
    }

    public List<Rating> getRatingsReceived() {
        return ratingsReceived;
    }

    public void setRatingsReceived(List<Rating> ratingsReceived) {
        this.ratingsReceived = ratingsReceived;
    }

    public List<PanicAlert> getPanicAlertsActivated() {
        return panicAlertsActivated;
    }

    public void setPanicAlertsActivated(List<PanicAlert> panicAlertsActivated) {
        this.panicAlertsActivated = panicAlertsActivated;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }
}
