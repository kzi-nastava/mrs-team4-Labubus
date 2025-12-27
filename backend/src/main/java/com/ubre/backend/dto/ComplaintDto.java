package com.ubre.backend.dto;

// For handling complaints made by users against drivers

public class ComplaintDto {
    private Long id;
    private Long driverId;
    private Long userId;
    private String text;

    public ComplaintDto() {
    }

    public ComplaintDto(Long id, Long driverId, Long userId, String text) {
        this.id = id;
        this.driverId = driverId;
        this.userId = userId;
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
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
}
