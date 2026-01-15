package com.ubre.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_stats")
public class UserStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonIgnore
    private User user;

    @Column(name = "active_past_24h", nullable = false)
    private Integer activePast24Hours = 0;  // in minutes

    @Column(name = "number_of_rides", nullable = false)
    private Integer numberOfRides = 0;

    @Column(name = "distance_traveled_km", nullable = false)
    private Double distanceTraveled = 0.0;

    @Column(name = "money_spent", nullable = false)
    private Double moneySpent = 0.0;

    @Column(name = "money_earned", nullable = false)
    private Double moneyEarned = 0.0;

    public UserStats() {}

    public UserStats(User user) {
        this.user = user;
    }
}
