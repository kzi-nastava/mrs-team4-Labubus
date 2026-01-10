package com.ubre.backend.model;

import com.ubre.backend.dto.ReviewDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String text;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ride_id", nullable = false)
    private Ride ride;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    // Constructors
    public Review() {
        this.createdAt = LocalDateTime.now();
    }

    public Review(Integer rating, String text, Ride ride, User user, Driver driver) {
        this.rating = rating;
        this.text = text;
        this.ride = ride;
        this.user = user;
        this.driver = driver;
        this.createdAt = LocalDateTime.now();
    }

    public Review(ReviewDto dto) {
        this.rating = dto.getRating();
        this.text = dto.getText();
        this.createdAt = LocalDateTime.now();
    }
}
