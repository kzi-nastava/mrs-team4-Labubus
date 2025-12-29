package com.ubre.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("REGISTERED_USER")
public class RegisteredUser extends User {

    @Column(name = "activation_token")
    private String activationToken;

    @Column(name = "activation_token_expiry")
    private LocalDateTime activationTokenExpiry;

    @Column(name = "is_activated")
    private boolean isActivated = false;

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
    private List<Ride> createdRides = new ArrayList<>();

    @ManyToMany(mappedBy = "passengers")
    private List<Ride> ridesAsPassenger = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Route> favoriteRoutes = new ArrayList<>();

    @OneToMany(mappedBy = "rater", cascade = CascadeType.ALL)
    private List<Rating> ratingsGiven = new ArrayList<>();

    @OneToMany(mappedBy = "reporter", cascade = CascadeType.ALL)
    private List<RideInconsistencyReport> reportsSubmitted = new ArrayList<>();

    @OneToMany(mappedBy = "activator", cascade = CascadeType.ALL)
    private List<PanicAlert> panicAlertsActivated = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Chat chat;

    // Constructors
    public RegisteredUser() {
        super();
    }

    public RegisteredUser(String email, String password, String firstName, String lastName, String address, String phoneNumber) {
        super(email, password, firstName, lastName, address, phoneNumber);
    }

    // Getters and Setters
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

    public boolean isActivated() {
        return isActivated;
    }

    public void setActivated(boolean activated) {
        isActivated = activated;
    }

    public List<Ride> getCreatedRides() {
        return createdRides;
    }

    public void setCreatedRides(List<Ride> createdRides) {
        this.createdRides = createdRides;
    }

    public List<Ride> getRidesAsPassenger() {
        return ridesAsPassenger;
    }

    public void setRidesAsPassenger(List<Ride> ridesAsPassenger) {
        this.ridesAsPassenger = ridesAsPassenger;
    }

    public List<Route> getFavoriteRoutes() {
        return favoriteRoutes;
    }

    public void setFavoriteRoutes(List<Route> favoriteRoutes) {
        this.favoriteRoutes = favoriteRoutes;
    }

    public List<Rating> getRatingsGiven() {
        return ratingsGiven;
    }

    public void setRatingsGiven(List<Rating> ratingsGiven) {
        this.ratingsGiven = ratingsGiven;
    }

    public List<RideInconsistencyReport> getReportsSubmitted() {
        return reportsSubmitted;
    }

    public void setReportsSubmitted(List<RideInconsistencyReport> reportsSubmitted) {
        this.reportsSubmitted = reportsSubmitted;
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
