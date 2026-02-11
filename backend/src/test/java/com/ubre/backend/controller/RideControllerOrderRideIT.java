package com.ubre.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubre.backend.dto.RideOrderDto;
import com.ubre.backend.dto.WaypointDto;
import com.ubre.backend.enums.Role;
import com.ubre.backend.enums.UserStatus;
import com.ubre.backend.enums.VehicleType;
import com.ubre.backend.model.Driver;
import com.ubre.backend.model.Passenger;
import com.ubre.backend.model.UserStats;
import com.ubre.backend.model.Vehicle;
import com.ubre.backend.repository.DriverRepository;
import com.ubre.backend.repository.RideRepository;
import com.ubre.backend.repository.UserRepository;
import com.ubre.backend.repository.VehicleRepository;
import com.ubre.backend.repository.WaypointRepository;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class RideControllerOrderRideIT extends AbstractTestNGSpringContextTests {

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


    @BeforeMethod
    @Transactional
    public void setUp() {
        cleanupDatabase();
    }

    @AfterMethod
    @Transactional
    public void tearDown() {
        cleanupDatabase();
    }

    @Test
    public void orderRide_happyPath_returns201_andJson() throws Exception {
        Passenger creator = createPassenger("creator-happy@test.com");
        createDriverWithVehicleAndStats("driver-happy@test.com", UserStatus.ACTIVE, VehicleType.STANDARD, true, true, 120);

        RideOrderDto dto = makeValidRideOrderDto(creator.getId());

        mockMvc.perform(post("/api/rides/order")
                .with(user("test").roles("REGISTERED_USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.status").value("PENDING"))
            .andExpect(jsonPath("$.driver.id").exists())
            .andExpect(jsonPath("$.passengers.length()").value(1))
            .andExpect(jsonPath("$.waypoints.length()").value(2))
            .andExpect(jsonPath("$.distance").value(1200.0))
            .andExpect(jsonPath("$.price").value(500.0));
    }

    @Test
    public void orderRide_guestRole_allowed_returns201() throws Exception {
        Passenger creator = createPassenger("creator-guest@test.com");
        createDriverWithVehicleAndStats("driver-guest@test.com", UserStatus.ACTIVE, VehicleType.STANDARD, false, false, 120);

        RideOrderDto dto = makeValidRideOrderDto(creator.getId());

        mockMvc.perform(post("/api/rides/order")
                .with(user("guest").roles("GUEST"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists());
    }

    @Test
    public void orderRide_missingCreatorId_returns400() throws Exception {
        RideOrderDto dto = makeValidRideOrderDto(null);

        mockMvc.perform(post("/api/rides/order")
                .with(user("test").roles("REGISTERED_USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(Matchers.containsString("Creator ID cannot be null")));
    }

    @Test
    public void orderRide_missingVehicleType_returns400() throws Exception {
        RideOrderDto dto = makeValidRideOrderDto(1L);
        dto.setVehicleType(null);

        mockMvc.perform(post("/api/rides/order")
                .with(user("test").roles("REGISTERED_USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(Matchers.containsString("Vehicle type cannot be null")));
    }

    @Test
    public void orderRide_missingDistance_returns400() throws Exception {
        RideOrderDto dto = makeValidRideOrderDto(1L);
        dto.setDistance(null);

        mockMvc.perform(post("/api/rides/order")
                .with(user("test").roles("REGISTERED_USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(Matchers.containsString("Distance cannot be null")));
    }

    @Test
    public void orderRide_missingRequiredTime_returns400() throws Exception {
        RideOrderDto dto = makeValidRideOrderDto(1L);
        dto.setRequiredTime(null);

        mockMvc.perform(post("/api/rides/order")
                .with(user("test").roles("REGISTERED_USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(Matchers.containsString("Required time cannot be null")));
    }

    @Test
    public void orderRide_missingPrice_returns400() throws Exception {
        RideOrderDto dto = makeValidRideOrderDto(1L);
        dto.setPrice(null);

        mockMvc.perform(post("/api/rides/order")
                .with(user("test").roles("REGISTERED_USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(Matchers.containsString("Price cannot be null")));
    }

    @Test
    public void orderRide_missingWaypoints_returns400() throws Exception {
        RideOrderDto dto = makeValidRideOrderDto(1L);
        dto.setWaypoints(null);

        mockMvc.perform(post("/api/rides/order")
                .with(user("test").roles("REGISTERED_USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(Matchers.containsString("Waypoints cannot be empty")));
    }

    @Test
    public void orderRide_emptyWaypoints_returns400() throws Exception {
        RideOrderDto dto = makeValidRideOrderDto(1L);
        dto.setWaypoints(new ArrayList<>());

        mockMvc.perform(post("/api/rides/order")
                .with(user("test").roles("REGISTERED_USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(Matchers.containsString("Waypoints cannot be empty")));
    }

    @Test
    public void orderRide_waypointMissingLatitude_returns400() throws Exception {
        Passenger creator = createPassenger("creator-lat@test.com");
        createDriverWithVehicleAndStats("driver-lat@test.com", UserStatus.ACTIVE, VehicleType.STANDARD, false, false, 120);

        RideOrderDto dto = makeValidRideOrderDto(creator.getId());
        dto.getWaypoints().get(0).setLatitude(null);

        mockMvc.perform(post("/api/rides/order")
                .with(user("test").roles("REGISTERED_USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(Matchers.containsString("Latitude cannot be null")));
    }

    @Test
    public void orderRide_waypointMissingLongitude_returns400() throws Exception {
        Passenger creator = createPassenger("creator-lon@test.com");
        createDriverWithVehicleAndStats("driver-lon@test.com", UserStatus.ACTIVE, VehicleType.STANDARD, false, false, 120);

        RideOrderDto dto = makeValidRideOrderDto(creator.getId());
        dto.getWaypoints().get(0).setLongitude(null);

        mockMvc.perform(post("/api/rides/order")
                .with(user("test").roles("REGISTERED_USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(Matchers.containsString("Longitude cannot be null")));
    }

    @Test
    public void orderRide_nullPassengersEmails_returns201() throws Exception {
        Passenger creator = createPassenger("creator-null-pass@test.com");
        createDriverWithVehicleAndStats("driver-null-pass@test.com", UserStatus.ACTIVE, VehicleType.STANDARD, false, false, 120);

        RideOrderDto dto = makeValidRideOrderDto(creator.getId());
        dto.setPassengersEmails(new ArrayList<>());

        mockMvc.perform(post("/api/rides/order")
                .with(user("test").roles("REGISTERED_USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isCreated());
    }

    @Test
    public void priceEstimate_standardVehicle_returns200() throws Exception {
        Map<String, Double> payload = Map.of("distance", 1500.0, "vehicleType", 0.0);

        mockMvc.perform(post("/api/rides/price-estimate")
                .with(user("test").roles("REGISTERED_USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isOk())
            .andExpect(content().string("6.8"));
    }

    @Test
    public void priceEstimate_vanVehicle_returns200() throws Exception {
        Map<String, Double> payload = Map.of("distance", 1000.0, "vehicleType", 1.0);

        mockMvc.perform(post("/api/rides/price-estimate")
                .with(user("test").roles("REGISTERED_USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isOk())
            .andExpect(content().string("9.2"));
    }

    @Test
    public void priceEstimate_missingDistance_returns4xx() throws Exception {
        String payload = "{\"vehicleType\":0,\"distance\":\"abc\"}";

        mockMvc.perform(post("/api/rides/price-estimate")
                .with(user("test").roles("REGISTERED_USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().is4xxClientError());
    }

    @Test
    public void priceEstimate_missingVehicleType_returns4xx() throws Exception {
        String payload = "{\"distance\":1000,\"vehicleType\":\"abc\"}";

        mockMvc.perform(post("/api/rides/price-estimate")
                .with(user("test").roles("REGISTERED_USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().is4xxClientError());
    }

    @Test
    public void priceEstimate_luxuryVehicle_returns200() throws Exception {
        Map<String, Double> payload = Map.of("distance", 1000.0, "vehicleType", 2.0);

        mockMvc.perform(post("/api/rides/price-estimate")
                .with(user("test").roles("REGISTERED_USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isOk())
            .andExpect(content().string("21.2"));
    }

    @Test
    public void priceEstimate_vehicleTypeOutOfRange_defaultsToLuxury() throws Exception {
        Map<String, Double> payload = Map.of("distance", 1000.0, "vehicleType", 5.0);

        mockMvc.perform(post("/api/rides/price-estimate")
                .with(user("test").roles("REGISTERED_USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isOk())
            .andExpect(content().string("21.2"));
    }

    private RideOrderDto makeValidRideOrderDto(Long creatorId) {
        List<WaypointDto> waypoints = List.of(
            new WaypointDto(null, "Start", 45.0, 19.0),
            new WaypointDto(null, "End", 46.0, 20.0)
        );
        RideOrderDto dto = new RideOrderDto();
        dto.setCreatorId(creatorId);
        dto.setPassengersEmails(new ArrayList<>());
        dto.setWaypoints(waypoints);
        dto.setVehicleType(VehicleType.STANDARD);
        dto.setBabyFriendly(false);
        dto.setPetFriendly(false);
        dto.setScheduledTime(null);
        dto.setDistance(1200.0);
        dto.setRequiredTime(600.0);
        dto.setPrice(500.0);
        return dto;
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

    private Driver createDriverWithVehicleAndStats(String email, UserStatus status, VehicleType type, boolean babyFriendly, boolean petFriendly, int activeMinutes) {
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
        vehicle.setType(type);
        vehicle.setSeats(4);
        vehicle.setPlates("PL-" + email.replace("@", "_"));
        vehicle.setBabyFriendly(babyFriendly);
        vehicle.setPetFriendly(petFriendly);
        vehicle.setDriver(driver);

        driver.setVehicle(vehicle);

        UserStats stats = new UserStats();
        stats.setUser(driver);
        stats.setActivePast24Hours(activeMinutes);
        driver.setStats(stats);

        return driverRepository.save(driver);
    }

    private void cleanupDatabase() {
        transactionTemplate.execute(status -> {
            entityManager.clear();

            entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

            entityManager.createNativeQuery("TRUNCATE TABLE ride_passengers").executeUpdate();
            entityManager.createNativeQuery("TRUNCATE TABLE rides_waypoints").executeUpdate();
            entityManager.createNativeQuery("TRUNCATE TABLE reviews").executeUpdate();
            entityManager.createNativeQuery("TRUNCATE TABLE complaints").executeUpdate();
            entityManager.createNativeQuery("TRUNCATE TABLE notifications").executeUpdate();
            entityManager.createNativeQuery("TRUNCATE TABLE user_stats").executeUpdate();
            entityManager.createNativeQuery("TRUNCATE TABLE user_status_records").executeUpdate();
            entityManager.createNativeQuery("TRUNCATE TABLE rides").executeUpdate();
            entityManager.createNativeQuery("TRUNCATE TABLE waypoints").executeUpdate();
            entityManager.createNativeQuery("TRUNCATE TABLE vehicles").executeUpdate();
            entityManager.createNativeQuery("TRUNCATE TABLE users_rides").executeUpdate();
            entityManager.createNativeQuery("TRUNCATE TABLE users").executeUpdate();

            entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
            return null;
        });
    }


}
