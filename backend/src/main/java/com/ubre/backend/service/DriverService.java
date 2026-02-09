package com.ubre.backend.service;

import com.ubre.backend.dto.DriverRegistrationDto;
import com.ubre.backend.dto.ProfileChangeDto;
import com.ubre.backend.dto.RideDto;
import com.ubre.backend.dto.UserDto;

import java.util.Collection;
import java.util.List;

public interface DriverService {
    Collection<UserDto> getAllDrivers();
    Collection<UserDto> getAvailableDrivers();
    UserDto getDriverById(Long id);
    void approveProfileChanges(Long id);
    void rejectProfileChanges(Long id);
    void toggleAvailability(Long id);
    UserDto createDriver(DriverRegistrationDto driverRegistrationDto);
    RideDto notifyDriver(Long rideId, Long driverId);
    void activateDriverAccount(String token, String email, String newPassword);
    void requestProfileChange(ProfileChangeDto profileChangeDto);
    List<ProfileChangeDto> getPendingProfileChanges();
    void approveProfileChange(Long id);
    void rejectProfileChange(Long id);
}
