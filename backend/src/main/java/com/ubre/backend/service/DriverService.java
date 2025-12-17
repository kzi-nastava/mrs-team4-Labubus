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

// Inner class for registerDriver method
class RegisterDTO {
    private String email;
    private String firstName;
    private String lastName;
    private String address;
    private String phoneNumber;
    private String drivingLicense;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getDrivingLicense() { return drivingLicense; }
    public void setDrivingLicense(String drivingLicense) { this.drivingLicense = drivingLicense; }
}
