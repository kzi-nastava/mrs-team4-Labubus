package com.ubre.backend.dto.ride;

import java.time.LocalDateTime;

public class RideDTO {
    private Long id;
    private String startAddress;
    private String endAddress;
    private LocalDateTime scheduledTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double estimatedDistance;
    private double actualDistance;
    private double totalPrice;
    private String rideStatus;
    private String vehicleType;
    private boolean allowsBabies;
    private boolean allowsPets;

    public RideDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStartAddress() { return startAddress; }
    public void setStartAddress(String startAddress) { this.startAddress = startAddress; }
    public String getEndAddress() { return endAddress; }
    public void setEndAddress(String endAddress) { this.endAddress = endAddress; }
    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public double getEstimatedDistance() { return estimatedDistance; }
    public void setEstimatedDistance(double estimatedDistance) { this.estimatedDistance = estimatedDistance; }
    public double getActualDistance() { return actualDistance; }
    public void setActualDistance(double actualDistance) { this.actualDistance = actualDistance; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public String getRideStatus() { return rideStatus; }
    public void setRideStatus(String rideStatus) { this.rideStatus = rideStatus; }
    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    public boolean isAllowsBabies() { return allowsBabies; }
    public void setAllowsBabies(boolean allowsBabies) { this.allowsBabies = allowsBabies; }
    public boolean isAllowsPets() { return allowsPets; }
    public void setAllowsPets(boolean allowsPets) { this.allowsPets = allowsPets; }
}
