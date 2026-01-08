package com.ubre.backend.dto;

// For leaving a review after a ride

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewDto {
    private Long id;
    private Long driverId;
    private Long userId;
    private Integer rating; // e.g., 1 to 5
    private String text;

    public ReviewDto() {
    }
    public ReviewDto(Long id, Long driverId, Long userId, Integer rating, String text) {
        this.id = id;
        this.driverId = driverId;
        this.userId = userId;
        this.rating = rating;
        this.text = text;
    }
}
