package com.example.ubre.ui.dtos;

// User statistics object to be displayed in user profile (only necessary attributes for now are extracted)

public class UserStatsDto { // extend later with other attributes
    private Long userId;
    private Integer activePast24Hours; // should be in minutes
    private Integer numberOfRides;
    private Double distanceTraveled;
    private Double moneySpent;
    private Double moneyEarned;


    public UserStatsDto(Long userId, Integer activePast24Hours, Integer numberOfRides, Double distanceTraveled, Double moneySpent, Double moneyEarned) {
        this.userId = userId;
        this.activePast24Hours = activePast24Hours;
        this.numberOfRides = numberOfRides;
        this.distanceTraveled = distanceTraveled;
        this.moneySpent = moneySpent;
        this.moneyEarned = moneyEarned;
    }

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getActivePast24Hours() {
        return activePast24Hours;
    }

    public void setActivePast24Hours(Integer activePast24Hours) {
        this.activePast24Hours = activePast24Hours;
    }
    public Double getDistanceTraveled() {
        return distanceTraveled;
    }
    public void setDistanceTraveled(Double distanceTraveled) {
        this.distanceTraveled = distanceTraveled;
    }
    public Double getMoneySpent() {
        return moneySpent;
    }
    public void setMoneySpent(Double moneySpent) {
        this.moneySpent = moneySpent;
    }

    public Double getMoneyEarned() {
        return moneyEarned;
    }

    public void setMoneyEarned(Double moneyEarned) {
        this.moneyEarned = moneyEarned;
    }

    public Integer getNumberOfRides() {
        return numberOfRides;
    }

    public void setNumberOfRides(Integer numberOfRides) {
        this.numberOfRides = numberOfRides;
    }
}
