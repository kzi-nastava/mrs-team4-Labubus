package com.example.ubre.ui.dtos;

// DTO for ride cancellation requests

public class CancellationDto {
    private Long rideId;
    private String reason;

    public CancellationDto() {
    }

    public CancellationDto(Long rideId, String reason) {
        this.rideId = rideId;
        this.reason = reason;
    }

    public Long getRideId() {
        return rideId;
    }
    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }
    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
}
