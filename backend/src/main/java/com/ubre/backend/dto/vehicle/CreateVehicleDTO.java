package com.ubre.backend.dto.vehicle;

public class CreateVehicleDTO {
    private String model;
    private String vehicleType;
    private String licensePlate;
    private int numberOfSeats;
    private boolean allowsBabies;
    private boolean allowsPets;

    public CreateVehicleDTO() {}

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
}
