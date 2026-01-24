package com.ubre.backend.dto;

// User statistics object to be displayed in user profile (only necessary attributes for now are extracted)

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserStatsDto { // extend later with other attributes
    private Long userId;
    private Integer activePast24Hours; // should be in minutes
    private Integer numberOfRides;
    private Double distanceTraveled;
    private Double moneySpent;
    private Double moneyEarned;

    public UserStatsDto(Long userId, int activePast24Hours, int numberOfRides, double distanceTraveled, double moneySpent, double moneyEarned) {
        this.userId = userId;
        this.activePast24Hours = activePast24Hours;
        this.numberOfRides = numberOfRides;
        this.distanceTraveled = distanceTraveled;
        this.moneySpent = moneySpent;
        this.moneyEarned = moneyEarned;
    }
}
