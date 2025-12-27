package com.example.ubre.ui.dtos;

// Ride progress updates, including current location and estimated time of arrival to destination

public class RideProgressDto {
    private int rideId;
    private WaypointDto location;
    private int estimatedTime;

    public RideProgressDto() {
    }
    public RideProgressDto(int rideId, WaypointDto location, int estimatedTime) {
        this.rideId = rideId;
        this.location = location;
        this.estimatedTime = estimatedTime;
    }
    public int getRideId() {
        return rideId;
    }
    public void setRideId(int rideId) {
        this.rideId = rideId;
    }
    public WaypointDto getLocation() {
        return location;
    }

    public void setLocation(WaypointDto location) {
        this.location = location;
    }

    public int getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(int estimatedTime) {
        this.estimatedTime = estimatedTime;
    }
}
