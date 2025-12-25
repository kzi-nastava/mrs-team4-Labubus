package com.example.ubre.ui.model;

public class UserStatsDto { // extend later with other attributes
    private int activePast24Hours; // should be in minutes

    public UserStatsDto(int activePast24Hours) {
        this.activePast24Hours = activePast24Hours;
    }

    public int getActivePast24Hours() {
        return activePast24Hours;
    }

    public void setActivePast24Hours(int activePast24Hours) {
        this.activePast24Hours = activePast24Hours;
    }
}
