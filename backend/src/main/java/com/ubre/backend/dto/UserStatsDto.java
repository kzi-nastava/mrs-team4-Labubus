package com.ubre.backend.dto;

// User statistics object to be displayed in user profile (only necessary attributes for now are extracted)

public class UserStatsDto { // extend later with other attributes
    private int activePast24Hours; // should be in minutes
    private int numberOfRides;
    private double distanceTraveled;
    private double moneySpent;
    private double moneyEarned;


    public UserStatsDto(int activePast24Hours, int numberOfRides, double distanceTraveled, double moneySpent, double moneyEarned) {
        this.activePast24Hours = activePast24Hours;
        this.numberOfRides = numberOfRides;
        this.distanceTraveled = distanceTraveled;
        this.moneySpent = moneySpent;
        this.moneyEarned = moneyEarned;
    }

    public int getActivePast24Hours() {
        return activePast24Hours;
    }

    public void setActivePast24Hours(int activePast24Hours) {
        this.activePast24Hours = activePast24Hours;
    }
    public double getDistanceTraveled() {
        return distanceTraveled;
    }
    public void setDistanceTraveled(int distanceTraveled) {
        this.distanceTraveled = distanceTraveled;
    }
    public double getMoneySpent() {
        return moneySpent;
    }
    public void setMoneySpent(int moneySpent) {
        this.moneySpent = moneySpent;
    }

    public double getMoneyEarned() {
        return moneyEarned;
    }

    public void setMoneyEarned(int moneyEarned) {
        this.moneyEarned = moneyEarned;
    }

    public int getNumberOfRides() {
        return numberOfRides;
    }

    public void setNumberOfRides(int numberOfRides) {
        this.numberOfRides = numberOfRides;
    }
}
