package com.ubre.backend.dto.ride;

import java.time.LocalDateTime;
import java.util.List;

public class CreateRideDTO {
    private String startAddress;
    private double startLatitude;
    private double startLongitude;
    private String endAddress;
    private double endLatitude;
    private double endLongitude;
    private LocalDateTime scheduledTime;
    private String vehicleType;
    private boolean allowsBabies;
    private boolean allowsPets;
    private List<String> passengerEmails;
    private List<WaypointDTO> waypoints;

    public CreateRideDTO() {}

    public String getStartAddress() { return startAddress; }
    public void setStartAddress(String startAddress) { this.startAddress = startAddress; }
    public double getStartLatitude() { return startLatitude; }
    public void setStartLatitude(double startLatitude) { this.startLatitude = startLatitude; }
    public double getStartLongitude() { return startLongitude; }
    public void setStartLongitude(double startLongitude) { this.startLongitude = startLongitude; }
    public String getEndAddress() { return endAddress; }
    public void setEndAddress(String endAddress) { this.endAddress = endAddress; }
    public double getEndLatitude() { return endLatitude; }
    public void setEndLatitude(double endLatitude) { this.endLatitude = endLatitude; }
    public double getEndLongitude() { return endLongitude; }
    public void setEndLongitude(double endLongitude) { this.endLongitude = endLongitude; }
    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }
    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    public boolean isAllowsBabies() { return allowsBabies; }
    public void setAllowsBabies(boolean allowsBabies) { this.allowsBabies = allowsBabies; }
    public boolean isAllowsPets() { return allowsPets; }
    public void setAllowsPets(boolean allowsPets) { this.allowsPets = allowsPets; }
    public List<String> getPassengerEmails() { return passengerEmails; }
    public void setPassengerEmails(List<String> passengerEmails) { this.passengerEmails = passengerEmails; }
    public List<WaypointDTO> getWaypoints() { return waypoints; }
    public void setWaypoints(List<WaypointDTO> waypoints) { this.waypoints = waypoints; }

    public static class WaypointDTO {
        private String address;
        private double latitude;
        private double longitude;
        private int orderIndex;

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public double getLatitude() { return latitude; }
        public void setLatitude(double latitude) { this.latitude = latitude; }
        public double getLongitude() { return longitude; }
        public void setLongitude(double longitude) { this.longitude = longitude; }
        public int getOrderIndex() { return orderIndex; }
        public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }
    }
}
