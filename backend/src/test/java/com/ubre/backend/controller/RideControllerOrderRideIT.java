package com.ubre.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubre.backend.dto.RideDto;
import com.ubre.backend.dto.RideOrderDto;
import com.ubre.backend.dto.UserDto;
import com.ubre.backend.dto.WaypointDto;
import com.ubre.backend.enums.RideStatus;
import com.ubre.backend.enums.Role;
import com.ubre.backend.enums.UserStatus;
import com.ubre.backend.enums.VehicleType;
import com.ubre.backend.exception.GlobalExceptionHandler;
import com.ubre.backend.service.RideService;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RideController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
public class RideControllerOrderRideIT extends AbstractTestNGSpringContextTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RideService rideService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void orderRide_happyPath_returns201_andJson() throws Exception {
        RideOrderDto dto = makeValidRideOrderDto(10L);
        RideDto mockRide = makeMockRideDto(99L, 10L, 20L);

        when(rideService.orderRide(any(RideOrderDto.class))).thenReturn(mockRide);

        mockMvc.perform(post("/api/rides/order")
                .with(user("test").roles("REGISTERED_USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(99))
            .andExpect(jsonPath("$.status").value("PENDING"))
            .andExpect(jsonPath("$.driver.id").value(20))
            .andExpect(jsonPath("$.passengers.length()").value(2))
            .andExpect(jsonPath("$.waypoints.length()").value(2))
            .andExpect(jsonPath("$.distance").value(1200.0))
            .andExpect(jsonPath("$.price").value(500.0));
    }

    @Test
    public void orderRide_guestRole_allowed_returns201() throws Exception {
        RideOrderDto dto = makeValidRideOrderDto(10L);
        RideDto mockRide = makeMockRideDto(99L, 10L, 20L);

        when(rideService.orderRide(any(RideOrderDto.class))).thenReturn(mockRide);

        mockMvc.perform(post("/api/rides/order")
                .with(user("guest").roles("GUEST"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(99));
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
        RideOrderDto dto = makeValidRideOrderDto(1L);
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
        RideOrderDto dto = makeValidRideOrderDto(1L);
        dto.getWaypoints().get(0).setLongitude(null);

        mockMvc.perform(post("/api/rides/order")
                .with(user("test").roles("REGISTERED_USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
            .andExpect(content().string(Matchers.containsString("Longitude cannot be null")));
    }

    @Test
    public void orderRide_nullPassengersEmails_stillOk_returns201() throws Exception {
        RideOrderDto dto = makeValidRideOrderDto(10L);
        dto.setPassengersEmails(null);
        RideDto mockRide = makeMockRideDto(99L, 10L, 20L);

        when(rideService.orderRide(any(RideOrderDto.class))).thenReturn(mockRide);

        mockMvc.perform(post("/api/rides/order")
                .with(user("test").roles("REGISTERED_USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(99));
    }

    @Test
    public void orderRide_serviceThrowsRuntimeException_throwsServletException_withMessage() throws Exception {
        RideOrderDto dto = makeValidRideOrderDto(1L);
        when(rideService.orderRide(any(RideOrderDto.class))).thenThrow(new RuntimeException("boom"));

        try {
            mockMvc.perform(post("/api/rides/order")
                    .with(user("test").roles("REGISTERED_USER"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)));
            org.testng.Assert.fail("Expected ServletException");
        } catch (jakarta.servlet.ServletException ex) {
            org.testng.Assert.assertTrue(ex.getCause() instanceof RuntimeException);
            org.testng.Assert.assertEquals(ex.getCause().getMessage(), "boom");
        }
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
        Map<String, Double> payload = Map.of("vehicleType", 0.0);

        mockMvc.perform(post("/api/rides/price-estimate")
                .with(user("test").roles("REGISTERED_USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().is4xxClientError());
    }

    @Test
    public void priceEstimate_missingVehicleType_returns4xx() throws Exception {
        Map<String, Double> payload = Map.of("distance", 1000.0);

        mockMvc.perform(post("/api/rides/price-estimate")
                .with(user("test").roles("REGISTERED_USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
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

    private RideDto makeMockRideDto(Long rideId, Long creatorId, Long driverId) {
        RideDto ride = new RideDto();
        ride.setId(rideId);
        ride.setStartTime("2026-02-11T12:00:00");
        ride.setEndTime("2026-02-11T12:10:00");
        ride.setWaypoints(List.of(
            new WaypointDto(null, "Start", 45.0, 19.0),
            new WaypointDto(null, "End", 46.0, 20.0)
        ));
        ride.setDriver(new UserDto(driverId, Role.DRIVER, null, "driver@test.com", "Driver", "Test", "333-444", "Driver Address", UserStatus.ACTIVE));
        ride.setPassengers(List.of(
            new UserDto(creatorId, Role.REGISTERED_USER, null, "creator@test.com", "Creator", "User", "111-222", "Creator Address", UserStatus.ACTIVE),
            new UserDto(55L, Role.REGISTERED_USER, null, "extra@test.com", "Extra", "User", "222-333", "Extra Address", UserStatus.ACTIVE)
        ));
        ride.setDistance(1200.0);
        ride.setPrice(500.0);
        ride.setStatus(RideStatus.PENDING);
        ride.setCreatedBy(creatorId);
        return ride;
    }
}
