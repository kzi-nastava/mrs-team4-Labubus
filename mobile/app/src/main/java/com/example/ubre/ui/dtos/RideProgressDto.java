package com.example.ubre.ui.dtos;

// Ride progress updates, including current location and estimated time of arrival to destination

public class RideProgressDto {
    private Long rideId;
    private WaypointDto location;
    private Integer estimatedTime;

    public RideProgressDto() {
    }
    public RideProgressDto(Long rideId, WaypointDto location, Integer estimatedTime) {
        this.rideId = rideId;
        this.location = location;
        this.estimatedTime = estimatedTime;
    }
    public Long getRideId() {
        return rideId;
    }
    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }
    public WaypointDto getLocation() {
        return location;
    }

    public void setLocation(WaypointDto location) {
        this.location = location;
    }

    public Integer getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(Integer estimatedTime) {
        this.estimatedTime = estimatedTime;
    }
}
