package com.example.ubre.ui.dtos;

// For sending request via email to join a ride

public class PassengerRequestDto {
    private Long rideId;
    private String email;

    public PassengerRequestDto() {
    }
    public PassengerRequestDto(Long rideId, String email) {
        this.rideId = rideId;
        this.email = email;
    }
    public Long getRideId() {
        return rideId;
    }
    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
