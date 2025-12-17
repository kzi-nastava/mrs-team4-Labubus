package com.ubre.backend.service.impl;

import com.ubre.backend.service.RideService;
import com.ubre.backend.dto.ride.CreateRideDTO;
import com.ubre.backend.dto.ride.RideDTO;
import com.ubre.backend.model.*;
import com.ubre.backend.repository.*;
import com.ubre.backend.enums.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RideServiceImpl implements RideService {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private RegisteredUserRepository userRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private VehicleTypePricingRepository pricingRepository;

    @Override
    public RideDTO createRide(Long userId, CreateRideDTO createRideDTO) {
        RegisteredUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Location startLocation = new Location(
                createRideDTO.getStartAddress(),
                createRideDTO.getStartLatitude(),
                createRideDTO.getStartLongitude()
        );
        Location endLocation = new Location(
                createRideDTO.getEndAddress(),
                createRideDTO.getEndLatitude(),
                createRideDTO.getEndLongitude()
        );

        locationRepository.save(startLocation);
        locationRepository.save(endLocation);

        Ride ride = new Ride();
        ride.setCreator(user);
        ride.setStartLocation(startLocation);
        ride.setEndLocation(endLocation);
        ride.setVehicleType(VehicleType.valueOf(createRideDTO.getVehicleType()));
        ride.setAllowsBabies(createRideDTO.isAllowsBabies());
        ride.setAllowsPets(createRideDTO.isAllowsPets());
        ride.setScheduledTime(createRideDTO.getScheduledTime());
        ride.setRideStatus(RideStatus.PENDING);

        double distance = calculateDistance(startLocation, endLocation);
        VehicleTypePricing pricing = pricingRepository
                .findByVehicleType(VehicleType.valueOf(createRideDTO.getVehicleType()))
                .orElseThrow(() -> new RuntimeException("Pricing not found"));
        
        double totalPrice = pricing.getBasePrice() + (distance * pricing.getPricePerKm());
        ride.setEstimatedDistance(distance);
        ride.setTotalPrice(totalPrice);
        ride.setBasePrice(pricing.getBasePrice());
        ride.setPricePerKm(pricing.getPricePerKm());

        Driver driver = findBestAvailableDriver(ride);
        if (driver != null) {
            ride.setDriver(driver);
            ride.setRideStatus(RideStatus.DRIVER_ASSIGNED);
        } else {
            ride.setRideStatus(RideStatus.REJECTED);
        }

        rideRepository.save(ride);
        return convertToDTO(ride);
    }

    @Override
    public RideDTO getRideById(Long id) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ride not found"));
        return convertToDTO(ride);
    }

    @Override
    public List<RideDTO> getUserRides(Long userId) {
        RegisteredUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Ride> rides = rideRepository.findByCreatorOrderByStartTimeDesc(user);
        return rides.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<RideDTO> getDriverRides(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        List<Ride> rides = rideRepository.findByDriverOrderByStartTimeDesc(driver);
        return rides.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public void acceptRide(Long rideId, Long driverId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        ride.setDriver(driver);
        ride.setRideStatus(RideStatus.ACCEPTED);
        rideRepository.save(ride);
    }

    @Override
    public void rejectRide(Long rideId, String reason) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));
        ride.setRideStatus(RideStatus.REJECTED);
        ride.setCancellationReason(reason);
        rideRepository.save(ride);
    }

    @Override
    public void startRide(Long rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));
        ride.setStartTime(LocalDateTime.now());
        ride.setRideStatus(RideStatus.IN_PROGRESS);
        rideRepository.save(ride);
    }

    @Override
    public void endRide(Long rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));
        ride.setEndTime(LocalDateTime.now());
        ride.setRideStatus(RideStatus.COMPLETED);
        rideRepository.save(ride);
    }

    @Override
    public void cancelRide(Long rideId, String reason) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));
        ride.setRideStatus(RideStatus.CANCELLED);
        ride.setCancellationReason(reason);
        rideRepository.save(ride);
    }

    @Override
    public void stopRideInProgress(Long rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));
        ride.setEndTime(LocalDateTime.now());
        ride.setRideStatus(RideStatus.COMPLETED);
        // update end location and recalculate price
        rideRepository.save(ride);
    }

    @Override
    public double estimateRidePrice(CreateRideDTO createRideDTO) {
        Location start = new Location(
                createRideDTO.getStartAddress(),
                createRideDTO.getStartLatitude(),
                createRideDTO.getStartLongitude()
        );
        Location end = new Location(
                createRideDTO.getEndAddress(),
                createRideDTO.getEndLatitude(),
                createRideDTO.getEndLongitude()
        );

        double distance = calculateDistance(start, end);
        VehicleTypePricing pricing = pricingRepository
                .findByVehicleType(VehicleType.valueOf(createRideDTO.getVehicleType()))
                .orElseThrow(() -> new RuntimeException("Pricing not found"));

        return pricing.getBasePrice() + (distance * pricing.getPricePerKm());
    }

    @Override
    public List<RideDTO> getRidesBetween(LocalDateTime start, LocalDateTime end) {
        List<Ride> rides = rideRepository.findRidesBetween(start, end);
        return rides.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private Driver findBestAvailableDriver(Ride ride) {
        List<Driver> availableDrivers = driverRepository.findAvailableDrivers(DriverStatus.ACTIVE);
        
        availableDrivers = availableDrivers.stream()
                .filter(d -> d.getVehicle() != null)
                .filter(d -> d.getVehicle().getVehicleType() == ride.getVehicleType())
                .filter(d -> !ride.isAllowsBabies() || d.getVehicle().isAllowsBabies())
                .filter(d -> !ride.isAllowsPets() || d.getVehicle().isAllowsPets())
                .filter(d -> d.getActiveHoursLast24h() < 8)
                .collect(Collectors.toList());

        if (availableDrivers.isEmpty()) {
            return null;
        }

        // closest driver based on location
        return availableDrivers.get(0);
    }

    private double calculateDistance(Location start, Location end) {
        final int EARTH_RADIUS = 6371; 

        double latDistance = Math.toRadians(end.getLatitude() - start.getLatitude());
        double lonDistance = Math.toRadians(end.getLongitude() - start.getLongitude());
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(start.getLatitude())) 
                * Math.cos(Math.toRadians(end.getLatitude()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS * c;
    }

    private RideDTO convertToDTO(Ride ride) {
        RideDTO dto = new RideDTO();
        dto.setId(ride.getId());
        dto.setStartAddress(ride.getStartLocation().getAddress());
        dto.setEndAddress(ride.getEndLocation().getAddress());
        dto.setScheduledTime(ride.getScheduledTime());
        dto.setStartTime(ride.getStartTime());
        dto.setEndTime(ride.getEndTime());
        dto.setEstimatedDistance(ride.getEstimatedDistance());
        dto.setActualDistance(ride.getActualDistance());
        dto.setTotalPrice(ride.getTotalPrice());
        dto.setRideStatus(ride.getRideStatus().name());
        dto.setVehicleType(ride.getVehicleType().name());
        dto.setAllowsBabies(ride.isAllowsBabies());
        dto.setAllowsPets(ride.isAllowsPets());
        return dto;
    }
}
