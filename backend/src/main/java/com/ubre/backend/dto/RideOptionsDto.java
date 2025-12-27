package com.example.ubre.ui.dtos;

import com.example.ubre.ui.enums.VehicleType;

// When ordering a ride, this DTO carries the selected ride options.

public class RideOptionsDto {
    private VehicleType vehicleType;
    private boolean babyFriendly;
    private boolean petFriendly;
    public RideOptionsDto(VehicleType vehicleType, boolean babyFriendly, boolean petFriendly) {
        this.vehicleType = vehicleType;
        this.babyFriendly = babyFriendly;
        this.petFriendly = petFriendly;
    }


    // getters
    public VehicleType getVehicleType() {
        return vehicleType;
    }
    public boolean isBabyFriendly() {
        return babyFriendly;
    }
    public boolean isPetFriendly() {
        return petFriendly;
    }


    // setters
    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }
    public void setBabyFriendly(boolean babyFriendly) {
        this.babyFriendly = babyFriendly;
    }
    public void setPetFriendly(boolean petFriendly) {
        this.petFriendly = petFriendly;
    }
}
