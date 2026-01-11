package com.ubre.backend.dto;

// For leaving a review after a ride

import com.ubre.backend.model.Review;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReviewDto {
    private Long id;
    private Long driverId;
    private Long userId;
    private Integer rating; // e.g., 1 to 5
    private String text;

    public ReviewDto(Long id, Long driverId, Long userId, Integer rating, String text) {
        this.id = id;
        this.driverId = driverId;
        this.userId = userId;
        this.rating = rating;
        this.text = text;
    }

    public ReviewDto(Review model) {
        this.id = model.getId();
        this.driverId = model.getDriver().getId();
        this.userId = model.getUser().getId();
        this.rating = model.getRating();
        this.text = model.getText();
    }
}
