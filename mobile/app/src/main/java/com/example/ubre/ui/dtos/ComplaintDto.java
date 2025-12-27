package com.example.ubre.ui.dtos;

// For handling complaints made by users against drivers

public class ComplaintDto {
    private int id;
    private int driverId;
    private int userId;
    private String text;

    public ComplaintDto() {
    }

    public ComplaintDto(int id, int driverId, int userId, String text) {
        this.id = id;
        this.driverId = driverId;
        this.userId = userId;
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
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
}
