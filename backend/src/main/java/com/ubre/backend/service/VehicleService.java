package com.ubre.backend.service;

import com.ubre.backend.dto.VehicleDto;
import com.ubre.backend.dto.VehicleIndicatorDto;

import java.util.Collection;

public interface VehicleService {
    VehicleDto createVehicle(Long driverId, VehicleDto createVehicleDto);
    VehicleDto getVehicleById(Long id);
    VehicleDto getVehicleByDriver(Long driverId);
    VehicleDto updateVehicle(Long id, VehicleDto updateVehicleDto);
    VehicleDto deleteVehicle(Long id);
    Collection<VehicleIndicatorDto> getVehicleIndicators();
    VehicleIndicatorDto getVehicleIndicator(Long id);
}
