package com.ubre.backend.dto;

// DTO for ride cancellation requests

public class CancellationDto {
    private int rideId;
    private String reason;

    public CancellationDto() {
    }

    public CancellationDto(int rideId, String reason) {
        this.rideId = rideId;
        this.reason = reason;
    }

    public int getRideId() {
        return rideId;
    }
    public void setRideId(int rideId) {
        this.rideId = rideId;
    }
    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
}
