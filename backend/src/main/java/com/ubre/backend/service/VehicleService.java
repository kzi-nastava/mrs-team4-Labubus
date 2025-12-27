package com.ubre.backend.service;

import com.ubre.backend.dto.VehicleDto;

public interface VehicleService {
    VehicleDto createVehicle(Long driverId, VehicleDto vehicleDto);
    VehicleDto getVehicleById(Long id);
    VehicleDto getVehicleByDriver(Long driverId);
    VehicleDto updateVehicle(Long id, VehicleDto vehicleDto);
    void deleteVehicle(Long id);
}
