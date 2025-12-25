package com.example.ubre.ui.model;

public class UserStatsDto { // extend later with other attributes
    private int activePast24Hours; // should be in minutes
    private int distanceTraveled;
    private int moneySpent;

    public UserStatsDto(int activePast24Hours, int distanceTraveled, int moneySpent) {
        this.activePast24Hours = activePast24Hours;
        this.distanceTraveled = distanceTraveled;
        this.moneySpent = moneySpent;
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
}
