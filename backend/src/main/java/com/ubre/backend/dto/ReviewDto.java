package com.ubre.backend.dto;

// For leaving a review after a ride

import com.ubre.backend.model.Review;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReviewDto {
    private Long id;
    private Long driverId;
    @NotNull(message = "User ID cannot be null")
    private Long userId;
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot be more than 5")
    private Integer rating; // e.g., 1 to 5
    @NotBlank(message = "Review text cannot be blank")
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
