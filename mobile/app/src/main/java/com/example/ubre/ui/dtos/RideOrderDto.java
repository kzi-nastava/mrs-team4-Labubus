package com.example.ubre.ui.dtos;

import com.example.ubre.ui.enums.VehicleType;

import java.util.List;

public class RideOrderDto {
    private Long id;
    private Long creatorId;
    private List<String> passengerEmails;
    private List<WaypointDto> waypoints;
    private VehicleType vehicleType;
    private Boolean babyFriendly;
    private Boolean petFriendly;
    public String scheduledTime;
    private Double distance;
    private Double requiredTime;
    private Double price;

    public RideOrderDto() {
    }

    public RideOrderDto(Long id, Long creatorId, List<String> passengerEmails, List<WaypointDto> waypoints,
                        VehicleType vehicleType, Boolean babyFriendly, Boolean petFriendly,
                        String scheduledTime, Double distance, Double requiredTime, Double price) {
        this.id = id;
        this.creatorId = creatorId;
        this.passengerEmails = passengerEmails;
        this.waypoints = waypoints;
        this.vehicleType = vehicleType;
        this.babyFriendly = babyFriendly;
        this.petFriendly = petFriendly;
        this.scheduledTime = scheduledTime;
        this.distance = distance;
        this.requiredTime = requiredTime;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public List<String> getPassengerEmails() {
        return passengerEmails;
    }

    public void setPassengerEmails(List<String> passengerEmails) {
        this.passengerEmails = passengerEmails;
    }

    public List<WaypointDto> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(List<WaypointDto> waypoints) {
        this.waypoints = waypoints;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Boolean getBabyFriendly() {
        return babyFriendly;
    }

    public void setBabyFriendly(Boolean babyFriendly) {
        this.babyFriendly = babyFriendly;
    }

    public Boolean getPetFriendly() {
        return petFriendly;
    }

    public void setPetFriendly(Boolean petFriendly) {
        this.petFriendly = petFriendly;
    }

    public String getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(String scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getRequiredTime() {
        return requiredTime;
    }

    public void setRequiredTime(Double requiredTime) {
        this.requiredTime = requiredTime;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
