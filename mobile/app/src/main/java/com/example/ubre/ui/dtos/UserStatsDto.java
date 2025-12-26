package com.example.ubre.ui.dtos;

public class UserStatsDto { // extend later with other attributes
    private int activePast24Hours; // should be in minutes
    private int numberOfRides;
    private int distanceTraveled;
    private int moneySpent;
    private int moneyEarned;


    public UserStatsDto(int activePast24Hours, int numberOfRides, int distanceTraveled, int moneySpent, int moneyEarned) {
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
    public int getDistanceTraveled() {
        return distanceTraveled;
    }
    public void setDistanceTraveled(int distanceTraveled) {
        this.distanceTraveled = distanceTraveled;
    }
    public int getMoneySpent() {
        return moneySpent;
    }
    public void setMoneySpent(int moneySpent) {
        this.moneySpent = moneySpent;
    }

    public int getMoneyEarned() {
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
