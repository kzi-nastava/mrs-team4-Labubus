package com.ubre.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubre.backend.dto.WaypointDto;
import com.ubre.backend.enums.RideStatus;
import com.ubre.backend.enums.Role;
import com.ubre.backend.enums.UserStatus;
import com.ubre.backend.enums.VehicleType;
import com.ubre.backend.model.*;
import com.ubre.backend.repository.RideRepository;
import com.ubre.backend.repository.UserRepository;
import com.ubre.backend.repository.VehicleRepository;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class RideControllerStopRideTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    private Long testRideId;
    private Driver driver;
    private Passenger passenger;

    @BeforeMethod
    @Transactional
    public void setUp() {

        driver = new Driver();
        driver.setEmail("driver@test.com");
        driver.setPassword("$2a$10$passwordhash");
        driver.setName("John");
        driver.setSurname("Driver");
        driver.setAddress("Adress");
        driver.setPhone("123456789");
        driver.setRole(Role.DRIVER);
        driver.setStatus(UserStatus.ON_RIDE);
        driver = userRepository.save(driver);

        passenger = new Passenger();
        passenger.setEmail("passenger@test.com");
        passenger.setPassword("$2a$10$passwordhash");
        passenger.setName("Jane");
        passenger.setSurname("Passenger");
        passenger.setAddress("Adress");
        passenger.setPhone("123456789");
        passenger.setRole(Role.REGISTERED_USER);
        passenger = userRepository.save(passenger);

        Vehicle vehicle = new Vehicle();
        vehicle.setType(VehicleType.STANDARD);
        vehicle.setDriver(driver);
        vehicle.setPlates("plate");
        vehicle.setModel("model");
        vehicle.setPetFriendly(true);
        vehicle.setBabyFriendly(false);
        vehicle.setSeats(4);
        vehicle = vehicleRepository.save(vehicle);

        driver.setVehicle(vehicle);
        driver = userRepository.save(driver);

        Ride ride = new Ride();
        ride.setDriver(driver);
        ride.setCreator(passenger);
        ride.setPassengers(new ArrayList<>(List.of(passenger)));
        ride.setStatus(RideStatus.IN_PROGRESS);
        ride.setStartTime(LocalDateTime.now().minusMinutes(10));
        ride.setPrice(30.0);
        ride.setDistance(20.0);
        ride.setFavorite(false);
        ride.setPanic(false);

        Waypoint visited = new Waypoint("Start Location", 45.0, 19.0);
        visited.setVisited(true);

        Waypoint unvisited = new Waypoint("Planned Destination", 45.2, 19.2);
        unvisited.setVisited(false);

        ride.setWaypoints(new ArrayList<>(List.of(visited, unvisited)));

        ride = rideRepository.save(ride);
        testRideId = ride.getId();

        Authentication auth = Mockito.mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(driver);
        when(auth.getName()).thenReturn(driver.getEmail());
        SecurityContext context = Mockito.mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
    }

    @Test(description = "Should successfully stop ride and return calculated price")
    public void shouldStopRideSuccessfully() throws Exception {

        WaypointDto stopLocation = new WaypointDto();
        stopLocation.setLabel("Actual Stop Location");
        stopLocation.setLatitude(45.1);
        stopLocation.setLongitude(19.1);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                driver, null, createAuthorityList("ROLE_DRIVER")
        );

        mockMvc.perform(put("/api/rides/{rideId}/stop", testRideId)
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stopLocation)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber())
                .andExpect(jsonPath("$").value(greaterThan(0.0)));

        Ride updatedRide = rideRepository.findById(testRideId).orElseThrow();
        assert updatedRide.getStatus() == RideStatus.COMPLETED;
        assert updatedRide.getEndTime() != null;
    }

    @Test(description = "Should handle concurrent stop requests gracefully")
    public void shouldHandleConcurrentStopRequests() throws Exception {

        WaypointDto stopLocation = new WaypointDto();
        stopLocation.setLabel("Stop");
        stopLocation.setLatitude(45.1);
        stopLocation.setLongitude(19.1);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                driver, null, createAuthorityList("ROLE_DRIVER")
        );

        mockMvc.perform(put("/api/rides/{rideId}/stop", testRideId)
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stopLocation)))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/rides/{rideId}/stop", testRideId)
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stopLocation)))
                .andExpect(status().isBadRequest());
    }

    @Test(description = "Should return 404 NOT_FOUND when ride does not exist")
    public void shouldReturn404WhenRideNotFound() throws Exception {
        WaypointDto stopLocation = new WaypointDto();
        stopLocation.setLabel("Stop");
        stopLocation.setLatitude(45.1);
        stopLocation.setLongitude(19.1);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                driver, null, createAuthorityList("ROLE_DRIVER")
        );

        mockMvc.perform(put("/api/rides/{rideId}/stop", 99999L)
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stopLocation)))
                .andExpect(status().isNotFound());
    }

    @Test(description = "Should throw exception when not authenticated")
    public void shouldThrowWhenNotAuthenticated() throws Exception {
        WaypointDto stopLocation = new WaypointDto();
        stopLocation.setLabel("Stop");
        stopLocation.setLatitude(45.1);
        stopLocation.setLongitude(19.1);

        mockMvc.perform(put("/api/rides/{rideId}/stop", testRideId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stopLocation)))
                .andExpect(status().isUnauthorized());
    }

    @Test(description = "Should return 400 BAD_REQUEST when ride is not in progress")
    public void shouldReturn400WhenRideNotInProgress() throws Exception {
        Ride ride = rideRepository.findById(testRideId).orElseThrow();
        ride.setStatus(RideStatus.COMPLETED);
        rideRepository.save(ride);

        WaypointDto stopLocation = new WaypointDto();
        stopLocation.setLabel("Stop");
        stopLocation.setLatitude(45.1);
        stopLocation.setLongitude(19.1);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                driver, null, createAuthorityList("ROLE_DRIVER")
        );

        mockMvc.perform(put("/api/rides/{rideId}/stop", testRideId)
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stopLocation)))
                .andExpect(status().isBadRequest());
    }

    @Test(description = "Should return 400 BAD_REQUEST when latitude is null")
    public void shouldReturn400WhenLatitudeIsNull() throws Exception {
        WaypointDto invalidWaypoint = new WaypointDto();
        invalidWaypoint.setLabel("Stop");
        invalidWaypoint.setLatitude(null);
        invalidWaypoint.setLongitude(19.1);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                driver, null, createAuthorityList("ROLE_DRIVER")
        );

        mockMvc.perform(put("/api/rides/{rideId}/stop", testRideId)
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidWaypoint)))
                .andExpect(status().isBadRequest());
    }

    @Test(description = "Should return 400 BAD_REQUEST when longitude is null")
    public void shouldReturn400WhenLongitudeIsNull() throws Exception {
        WaypointDto invalidWaypoint = new WaypointDto();
        invalidWaypoint.setLabel("Stop");
        invalidWaypoint.setLatitude(45.1);
        invalidWaypoint.setLongitude(null);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                driver, null, createAuthorityList("ROLE_DRIVER")
        );

        mockMvc.perform(put("/api/rides/{rideId}/stop", testRideId)
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidWaypoint)))
                .andExpect(status().isBadRequest());
    }

    @Test(description = "Should return 400 BAD_REQUEST when request body is empty")
    public void shouldReturn400WhenRequestBodyEmpty() throws Exception {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                driver, null, createAuthorityList("ROLE_DRIVER")
        );
        mockMvc.perform(put("/api/rides/{rideId}/stop", testRideId)
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test(description = "Should return 400 BAD_REQUEST when JSON is malformed")
    public void shouldReturn400WhenJsonMalformed() throws Exception {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                driver, null, createAuthorityList("ROLE_DRIVER")
        );
        mockMvc.perform(put("/api/rides/{rideId}/stop", testRideId)
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json}"))
                .andExpect(status().isBadRequest());
    }

    @Test(description = "Should accept waypoint without label")
    public void shouldAcceptWaypointWithoutLabel() throws Exception {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                driver, null, createAuthorityList("ROLE_DRIVER")
        );

        WaypointDto waypointNoLabel = new WaypointDto();
        waypointNoLabel.setLabel(null);
        waypointNoLabel.setLatitude(45.1);
        waypointNoLabel.setLongitude(19.1);

        mockMvc.perform(put("/api/rides/{rideId}/stop", testRideId)
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(waypointNoLabel)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber());
    }

}
