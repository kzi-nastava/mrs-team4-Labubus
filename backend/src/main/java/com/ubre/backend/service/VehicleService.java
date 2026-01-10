package com.ubre.backend.service;

import com.ubre.backend.dto.VehicleDto;
import com.ubre.backend.dto.VehicleIndicatorDto;
import com.ubre.backend.dto.WaypointDto;
import org.springframework.web.ErrorResponseException;

import java.util.Collection;

public interface VehicleService {
    VehicleDto createVehicle(VehicleDto createVehicleDto, Long userId) throws ErrorResponseException;
    VehicleDto getVehicleById(Long id) throws ErrorResponseException;
    VehicleDto getVehicleByDriver(Long driverId) throws ErrorResponseException;
    VehicleDto updateVehicle(Long id, VehicleDto updateVehicleDto) throws ErrorResponseException;
    VehicleDto deleteVehicle(Long id) throws ErrorResponseException;
    Collection<VehicleIndicatorDto> getVehicleIndicators();
    VehicleIndicatorDto getVehicleIndicator(Long id) throws ErrorResponseException;
    VehicleIndicatorDto setVehicleIndicator(Long id, WaypointDto waypointDto) throws ErrorResponseException;
}
