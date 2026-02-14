package com.ubre.backend.service;

import com.ubre.backend.dto.WaypointDto;
import com.ubre.backend.enums.RideStatus;
import com.ubre.backend.enums.UserStatus;
import com.ubre.backend.enums.VehicleType;
import com.ubre.backend.model.*;
import com.ubre.backend.repository.RideRepository;
import com.ubre.backend.repository.UserRepository;
import com.ubre.backend.service.impl.RideServiceImpl;
import com.ubre.backend.websocket.WebSocketNotificationService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class RideServiceStopRideTest {

    @Mock
    private RideRepository rideRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private Authentication authentication;
    @Mock
    private WebSocketNotificationService webSocketNotificationService;
    @Mock
    private EmailService emailService;
    @InjectMocks
    private RideServiceImpl rideService;
    private Ride testRide;
    private Driver driver;
    private Passenger passenger;
    private WaypointDto waypointDto;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        driver = new Driver();
        driver.setId(1L);
        driver.setEmail("driver@test.com");
        driver.setStatus(UserStatus.ON_RIDE);

        Vehicle vehicle = new Vehicle();
        vehicle.setType(VehicleType.STANDARD);
        driver.setVehicle(vehicle);

        passenger = new Passenger();
        passenger.setId(2L);
        passenger.setEmail("passenger@test.com");

        testRide = new Ride();
        testRide.setId(100L);
        testRide.setDriver(driver);
        testRide.setCreator(passenger);
        testRide.setPassengers(new ArrayList<>(List.of(passenger)));
        testRide.setStatus(RideStatus.IN_PROGRESS);
        testRide.setWaypoints(new ArrayList<>());
        testRide.setStartTime(LocalDateTime.now().minusMinutes(10));

        waypointDto = new WaypointDto();
        waypointDto.setLabel("Stop location");
        waypointDto.setLatitude(45.2551);
        waypointDto.setLongitude(19.8451);
        waypointDto.setVisited(true);

        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(driver);

        when(authentication.getName()).thenReturn(driver.getEmail());
        when(userRepository.findByEmail("driver@test.com")).thenReturn(Optional.of(driver));
    }

    @Test(description = "Should stop ride and calculate new price when stopped mid-route")
    public void shouldStopRideMidRoute() {
        Waypoint start = new Waypoint("Pickup", 45.0, 19.0);
        start.setVisited(true);

        Waypoint middle = new Waypoint("Middle Stop", 45.05, 19.0);
        middle.setVisited(true);

        Waypoint plannedEnd = new Waypoint("Original Destination", 45.2, 19.2);
        plannedEnd.setVisited(false);

        testRide.getWaypoints().add(start);
        testRide.getWaypoints().add(middle);
        testRide.getWaypoints().add(plannedEnd);

        when(rideRepository.findById(100L)).thenReturn(Optional.of(testRide));
        when(rideRepository.save(any(Ride.class))).thenReturn(testRide);

        Double newPrice = rideService.stopRideInProgress(100L, waypointDto);

        Assert.assertEquals(testRide.getWaypoints().size(), 3);

        boolean hasOriginalDestination = testRide.getWaypoints().stream()
                .anyMatch(w -> "Original Destination".equals(w.getLabel()));

        // Confirm original destination is removed
        Assert.assertFalse(hasOriginalDestination,
                "Original destination should be removed after stopping ride");

        // Confirm all unvisited waypoints are removed
        boolean hasUnvisited = testRide.getWaypoints().stream()
                .anyMatch(w -> w.getVisited() != null && !w.getVisited());
        Assert.assertFalse(hasUnvisited, "Unvisited waypoints should be removed");

        // Confirm end time is set
        Assert.assertNotNull(testRide.getEndTime());

        // Confirm price is recalculated
        Assert.assertNotNull(newPrice);
        Assert.assertTrue(newPrice > 0 , "Price should be calculated");

        // Confirm last waypoint is the stop location
        Waypoint lastWaypoint = testRide.getWaypoints().get(2);
        Assert.assertEquals(lastWaypoint.getLabel(), "Stop location");

        // Confirm ride is completed
        Assert.assertEquals(testRide.getStatus(), RideStatus.COMPLETED);

        // Confirm the driver is active again
        Assert.assertEquals(testRide.getDriver().getStatus(), UserStatus.ACTIVE);

        verify(rideRepository).save(testRide);
    }

    @Test(description = "Should calculate lower price for shorter distance")
    public void shouldCalculateLowerPriceForShorterDistance() {

        Waypoint start = new Waypoint("Start", 45.0, 19.0);
        start.setVisited(true);

        Waypoint earlyStop = new Waypoint("Stop 1", 45.09, 19.0);
        earlyStop.setVisited(true);

        Waypoint plannedEnd = new Waypoint("End", 45.27, 19.15);
        plannedEnd.setVisited(false);

        testRide.getWaypoints().add(start);
        testRide.getWaypoints().add(earlyStop);
        testRide.getWaypoints().add(plannedEnd);

        when(rideRepository.findById(100L)).thenReturn(Optional.of(testRide));
        when(rideRepository.save(any(Ride.class))).thenReturn(testRide);

        Double originalPrice = rideService.stopRideInProgress(100L, waypointDto);
        Double originalDistance = testRide.getDistance();

        testRide.setStatus(RideStatus.IN_PROGRESS);
        earlyStop.setVisited(false);

        testRide.setWaypoints(new ArrayList<>());

        testRide.getWaypoints().add(start);
        testRide.getWaypoints().add(earlyStop);
        testRide.getWaypoints().add(plannedEnd);

        when(rideRepository.findById(100L)).thenReturn(Optional.of(testRide));
        when(rideRepository.save(any(Ride.class))).thenReturn(testRide);

        Double newPrice = rideService.stopRideInProgress(100L, waypointDto);
        Double newDistance = testRide.getDistance();

        // Distance should be less
        Assert.assertTrue(newDistance < originalDistance,
                String.format("Original distance (%.2f) should be greater than early stop (%.2f)",
                        originalDistance, newDistance));

        // Price should be less
        Assert.assertTrue(newPrice < originalPrice, String.format("Original price (%.2f $) should be greater than early stop (%.2f $)",
                originalPrice, newPrice));
    }

    @Test(description = "Should throw exception when ride not found")
    public void shouldThrowWhenRideNotFound() {
        when(rideRepository.findById(999L)).thenReturn(Optional.empty());

        try {
            rideService.stopRideInProgress(999L, waypointDto);
            Assert.fail("Expected ResponseStatusException");
        } catch (ResponseStatusException ex) {
            Assert.assertEquals(ex.getStatusCode(), HttpStatus.NOT_FOUND);
        }
    }

    @Test(description = "Should throw FORBIDDEN when user is not the driver")
    public void shouldThrowWhenNotDriver() {
        Driver otherDriver = new Driver();
        otherDriver.setId(999L);
        otherDriver.setEmail("otherDriver@email.com");
        when(authentication.getName()).thenReturn(otherDriver.getEmail());
        when(userRepository.findByEmail(otherDriver.getEmail())).thenReturn(Optional.of(otherDriver));
        when(rideRepository.findById(100L)).thenReturn(Optional.of(testRide));

        try {
            rideService.stopRideInProgress(100L, waypointDto);
            Assert.fail("Expected ResponseStatusException");
        } catch (ResponseStatusException ex) {
            Assert.assertEquals(ex.getStatusCode(), HttpStatus.FORBIDDEN);
        }
    }

    @Test(description = "Should throw BAD_REQUEST when ride is not in progress")
    public void shouldThrowWhenRideNotInProgress() {
        testRide.setStatus(RideStatus.COMPLETED);
        when(rideRepository.findById(100L)).thenReturn(Optional.of(testRide));
        try {
            rideService.stopRideInProgress(100L, waypointDto);
            Assert.fail("Expected ResponseStatusException");
        } catch (ResponseStatusException ex) {
            Assert.assertEquals(ex.getStatusCode(), HttpStatus.BAD_REQUEST);
        }
    }

}