package com.ubre.backend.dto;

import com.ubre.backend.enums.DriverStatus;

// Every vehicle that is visible on the map has some indicator info

public class VehicleIndicatorDto {
    private Long driverId;
    private WaypointDto location;
    private DriverStatus status;
    private Boolean panic;

    public VehicleIndicatorDto() {
    }

    public VehicleIndicatorDto(Long driverId, WaypointDto location, DriverStatus status, Boolean panic) {
        this.driverId = driverId;
        this.location = location;
        this.status = status;
        this.panic = panic;
    }

    public Long getDriverId() {
        return driverId;
    }
    public void setDriverId(Long driverId) {
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

    public Boolean isPanic() {
        return panic;
    }
    public void setPanic(Boolean panic) {
        this.panic = panic;
    }

}
