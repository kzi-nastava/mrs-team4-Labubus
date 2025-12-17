package com.ubre.backend.service;

import com.ubre.backend.dto.vehicle.VehicleDTO;
import com.ubre.backend.dto.vehicle.CreateVehicleDTO;

public interface VehicleService {
    VehicleDTO createVehicle(Long driverId, CreateVehicleDTO createVehicleDTO);
    VehicleDTO getVehicleById(Long id);
    VehicleDTO getVehicleByDriver(Long driverId);
    VehicleDTO updateVehicle(Long id, CreateVehicleDTO updateVehicleDTO);
    void deleteVehicle(Long id);
}
