package com.ubre.backend.dto.vehicle;

public class VehicleDTO {
    private Long id;
    private String model;
    private String vehicleType;
    private String licensePlate;
    private int numberOfSeats;
    private boolean allowsBabies;
    private boolean allowsPets;
    private boolean isActive;

    public VehicleDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    public int getNumberOfSeats() { return numberOfSeats; }
    public void setNumberOfSeats(int numberOfSeats) { this.numberOfSeats = numberOfSeats; }
    public boolean isAllowsBabies() { return allowsBabies; }
    public void setAllowsBabies(boolean allowsBabies) { this.allowsBabies = allowsBabies; }
    public boolean isAllowsPets() { return allowsPets; }
    public void setAllowsPets(boolean allowsPets) { this.allowsPets = allowsPets; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
