package com.example.ubre.ui.dtos;

import com.example.ubre.ui.enums.VehicleType;

// When ordering a ride, this DTO carries the selected ride options.

public class RideOptionsDto {
    private VehicleType vehicleType;
    private Boolean babyFriendly;
    private Boolean petFriendly;
    public RideOptionsDto(VehicleType vehicleType, Boolean babyFriendly, Boolean petFriendly) {
        this.vehicleType = vehicleType;
        this.babyFriendly = babyFriendly;
        this.petFriendly = petFriendly;
    }


    // getters
    public VehicleType getVehicleType() {
        return vehicleType;
    }
    public Boolean isBabyFriendly() {
        return babyFriendly;
    }
    public Boolean isPetFriendly() {
        return petFriendly;
    }


    // setters
    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }
    public void setBabyFriendly(Boolean babyFriendly) {
        this.babyFriendly = babyFriendly;
    }
    public void setPetFriendly(Boolean petFriendly) {
        this.petFriendly = petFriendly;
    }
}
