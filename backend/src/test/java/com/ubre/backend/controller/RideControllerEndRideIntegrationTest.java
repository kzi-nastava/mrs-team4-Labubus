package com.ubre.backend.controller;

import com.ubre.backend.dto.WaypointDto;
import com.ubre.backend.enums.RideStatus;
import com.ubre.backend.enums.Role;
import com.ubre.backend.enums.UserStatus;
import com.ubre.backend.enums.VehicleType;
import com.ubre.backend.model.*;
import com.ubre.backend.repository.*;
import jakarta.persistence.EntityManager;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.http.MediaType;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import tools.jackson.databind.ObjectMapper;

import org.hamcrest.Matchers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class RideControllerEndRideIntegrationTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private WaypointRepository waypointRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private org.springframework.transaction.support.TransactionTemplate transactionTemplate;

    private Driver DRIVER;
    private User CREATOR;
    private WaypointDto END_LOCATION;

    @BeforeClass
    public void setup() {
        DRIVER = createDriverWithVehicle("driver@test.com", UserStatus.ON_RIDE);
        CREATOR = createPassenger("passenger@test.com");
        END_LOCATION = new WaypointDto();
        END_LOCATION.setLatitude(45.2671);
        END_LOCATION.setLongitude(19.8335);

        driverRepository.save(DRIVER);
        userRepository.save(CREATOR);

        Authentication auth = Mockito.mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(DRIVER);
        when(auth.getName()).thenReturn(DRIVER.getEmail());
        SecurityContext context = Mockito.mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
    }

    @AfterClass
    public void cleanup() {
        cleanupDatabase();
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    public void successfully_end_ride() throws Exception {
        Ride ride = createRide(CREATOR, DRIVER, RideStatus.IN_PROGRESS, LocalDateTime.now().minusMinutes(5));
        rideRepository.save(ride);

        mockMvc.perform(put("/api/rides/{id}/stop", ride.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(END_LOCATION)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(500.0));
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    public void end_missing_ride() throws Exception {
        mockMvc.perform(put("/api/rides/{id}/stop", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(END_LOCATION)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(Matchers.containsString("Ride not found")));
    }

    @Test
    @WithMockUser(roles = "REGISTERED_USER")
    public void end_ride_with_wrong_role() throws Exception {
        mockMvc.perform(put("/api/rides/{id}/stop", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(END_LOCATION)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    public void end_ride_by_wrong_driver() throws Exception {
        Driver otherDriver = createDriverWithVehicle("driver2@test.com", UserStatus.ON_RIDE);

        Ride ride = createRide(CREATOR, otherDriver, RideStatus.IN_PROGRESS, LocalDateTime.now().minusMinutes(5));
        rideRepository.save(ride);

        mockMvc.perform(put("/api/rides/{id}/stop", ride.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(END_LOCATION)))
                .andExpect(status().isForbidden())
                .andExpect(content().string(Matchers.containsString("Not your ride")));
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    public void end_pending_ride() throws Exception {
        Ride ride = createRide(CREATOR, DRIVER, RideStatus.PENDING, LocalDateTime.now().plusMinutes(5));
        rideRepository.save(ride);

        mockMvc.perform(put("/api/rides/{id}/stop", ride.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(END_LOCATION)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(Matchers.containsString("Ride is not in progress")));
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    public void end_ride_with_invalid_end_location() throws Exception {
        Ride ride = createRide(CREATOR, DRIVER, RideStatus.IN_PROGRESS, LocalDateTime.now().minusMinutes(5));
        rideRepository.save(ride);

        mockMvc.perform(put("/api/rides/{id}/stop", ride.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(null)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(500.0));
    }

    private void cleanupDatabase() {
        entityManager.flush();
        entityManager.createNativeQuery("DELETE FROM ride_passengers").executeUpdate();
        rideRepository.deleteAll();
        waypointRepository.deleteAll();
        vehicleRepository.deleteAll();
        entityManager.createNativeQuery("DELETE FROM user_stats").executeUpdate();
        userRepository.deleteAll();
        entityManager.flush();
    }

    private Passenger createPassenger(String email) {
        Passenger passenger = new Passenger();
        passenger.setRole(Role.REGISTERED_USER);
        passenger.setEmail(email);
        passenger.setPassword("pass123");
        passenger.setName("Test");
        passenger.setSurname("User");
        passenger.setAddress("Test Address");
        passenger.setPhone("111-222");
        passenger.setStatus(UserStatus.ACTIVE);
        passenger.setIsActivated(true);
        passenger.setIsBlocked(false);
        return userRepository.save(passenger);
    }

    private Driver createDriverWithVehicle(String email, UserStatus status) {
        Driver driver = new Driver();
        driver.setRole(Role.DRIVER);
        driver.setEmail(email);
        driver.setPassword("pass123");
        driver.setName("Driver");
        driver.setSurname("Test");
        driver.setAddress("Driver Address");
        driver.setPhone("333-444");
        driver.setStatus(status);
        driver.setIsActivated(true);
        driver.setIsBlocked(false);

        Vehicle vehicle = new Vehicle();
        vehicle.setModel("Model X");
        vehicle.setType(VehicleType.STANDARD);
        vehicle.setSeats(4);
        vehicle.setPlates("PL-" + email.replace("@", "_"));
        vehicle.setBabyFriendly(false);
        vehicle.setPetFriendly(false);
        vehicle.setDriver(driver);

        driver.setVehicle(vehicle);

        return driverRepository.save(driver);
    }

    private Ride createRide(User creator, Driver driver, RideStatus status, LocalDateTime startTime) {
        Ride ride = new Ride();
        ride.setCreator(creator);
        ride.setDriver(driver);
        ride.setStatus(status);
        ride.setStartTime(startTime);
        ride.setEndTime(null);
        ride.setWaypoints(List.of(new Waypoint("A", 45.0, 19.0, true), new Waypoint("B", 46.0, 20.0, true)));
        ride.setPassengers(List.of(creator));
        ride.setDistance(1200.0);
        ride.setPrice(500.0);
        ride.setPanic(false);
        ride.setFavorite(false);
        return rideRepository.save(ride);
    }
}
