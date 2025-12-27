package com.ubre.backend.service;

import com.ubre.backend.dto.UserDto;

import java.util.Collection;

public interface DriverService {
    Collection<UserDto> getAllDrivers();
    Collection<UserDto> getAvailableDrivers();
    UserDto getDriverById(Long id);
    void approveProfileChanges(Long id);
    void rejectProfileChanges(Long id);
    void toggleAvailability(Long id);
}
