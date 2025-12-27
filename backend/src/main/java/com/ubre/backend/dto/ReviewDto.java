package com.example.ubre.ui.dtos;

// For leaving a review after a ride

public class ReviewDto {
    private int id;
    private int driverId;
    private int userId;
    private int rating; // e.g., 1 to 5
    private String text;

    public ReviewDto() {
    }
    public ReviewDto(int id, int driverId, int userId, int rating, String text) {
        this.id = id;
        this.driverId = driverId;
        this.userId = userId;
        this.rating = rating;
        this.text = text;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getDriverId() {
        return driverId;
    }
    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public int getRating() {
        return rating;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
}
