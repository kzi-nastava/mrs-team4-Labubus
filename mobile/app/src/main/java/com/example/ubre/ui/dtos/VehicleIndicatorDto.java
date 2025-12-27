package com.example.ubre.ui.dtos;

import com.example.ubre.ui.enums.DriverStatus;

// Every vehicle that is visible on the map has some indicator info

public class VehicleIndicatorDto {
    private int driverId;
    private WaypointDto location;
    private DriverStatus status;
    private boolean panic;

    public VehicleIndicatorDto() {
    }

    public VehicleIndicatorDto(int driverId, WaypointDto location, DriverStatus status, boolean panic) {
        this.driverId = driverId;
        this.location = location;
        this.status = status;
        this.panic = panic;
    }

    public int getDriverId() {
        return driverId;
    }
    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public WaypointDto getLocation() {
        return location;
    }
    public void setLocation(WaypointDto location) {
        this.location = location;
    }

    public DriverStatus getStatus() {
        return status;
    }
    public void setStatus(DriverStatus status) {
        this.status = status;
    }

    public boolean isPanic() {
        return panic;
    }
    public void setPanic(boolean panic) {
        this.panic = panic;
    }

}
