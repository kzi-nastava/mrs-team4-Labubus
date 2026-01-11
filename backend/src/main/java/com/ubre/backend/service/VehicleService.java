package com.ubre.backend.service;

import com.ubre.backend.dto.VehicleDto;
import com.ubre.backend.dto.VehicleIndicatorDto;
import com.ubre.backend.dto.WaypointDto;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;

public interface VehicleService {
    VehicleDto createVehicle(VehicleDto createVehicleDto, Long userId) throws ResponseStatusException;
    VehicleDto getVehicleById(Long id) throws ResponseStatusException;
    VehicleDto getVehicleByDriver(Long driverId) throws ResponseStatusException;
    VehicleDto updateVehicle(Long id, VehicleDto updateVehicleDto) throws ResponseStatusException;
    VehicleDto deleteVehicle(Long id) throws ResponseStatusException;
    Collection<VehicleIndicatorDto> getVehicleIndicators();
    VehicleIndicatorDto getVehicleIndicator(Long id) throws ResponseStatusException;
    VehicleIndicatorDto setVehicleIndicator(Long id, WaypointDto waypointDto) throws ResponseStatusException;
}
