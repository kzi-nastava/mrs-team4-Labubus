package com.example.ubre.ui.dtos;

public class PassengerRequestDto {
    private int rideId;
    private String email;

    public PassengerRequestDto() {
    }
    public PassengerRequestDto(int rideId, String email) {
        this.rideId = rideId;
        this.email = email;
    }
    public int getRideId() {
        return rideId;
    }
    public void setRideId(int rideId) {
        this.rideId = rideId;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
