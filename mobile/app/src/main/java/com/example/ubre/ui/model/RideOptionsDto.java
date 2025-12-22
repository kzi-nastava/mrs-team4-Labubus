package com.example.ubre.ui.model;

public class RideOptionsDto {
    private VehicleType vehicleType;
    private boolean babyFriendly;
    private boolean petFriendly;
    public RideOptionsDto(VehicleType vehicleType, boolean babyFriendly, boolean petFriendly) {
        this.vehicleType = vehicleType;
        this.babyFriendly = babyFriendly;
        this.petFriendly = petFriendly;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public boolean isBabyFriendly() {
        return babyFriendly;
    }

    public boolean isPetFriendly() {
        return petFriendly;
    }
}
