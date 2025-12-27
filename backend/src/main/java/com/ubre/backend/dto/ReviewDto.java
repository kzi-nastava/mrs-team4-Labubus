package com.ubre.backend.dto;

// For leaving a review after a ride

public class ReviewDto {
    private Long id;
    private Long driverId;
    private Long userId;
    private Long rating; // e.g., 1 to 5
    private String text;

    public ReviewDto() {
    }
    public ReviewDto(Long id, Long driverId, Long userId, Long rating, String text) {
        this.id = id;
        this.driverId = driverId;
        this.userId = userId;
        this.rating = rating;
        this.text = text;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getDriverId() {
        return driverId;
    }
    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public Long getRating() {
        return rating;
    }
    public void setRating(Long rating) {
        this.rating = rating;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
}
