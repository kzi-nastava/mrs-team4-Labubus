package com.ubre.backend.service;

import com.ubre.backend.dto.user.DriverDTO;
import com.ubre.backend.dto.vehicle.CreateVehicleDTO;
import java.util.List;

public interface DriverService {
    DriverDTO createDriver(RegisterDTO driverData, CreateVehicleDTO vehicleData);
    void setInitialPassword(String token, String password);
    void toggleAvailability(Long driverId);
    DriverDTO getDriverById(Long id);
    List<DriverDTO> getAllDrivers();
    List<DriverDTO> getAvailableDrivers();
    void approveProfileChanges(Long driverId);
    void rejectProfileChanges(Long driverId);
}

