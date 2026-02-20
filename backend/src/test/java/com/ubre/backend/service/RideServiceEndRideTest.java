package com.ubre.backend.service;

import com.ubre.backend.dto.WaypointDto;
import com.ubre.backend.enums.NotificationType;
import com.ubre.backend.enums.RideStatus;
import com.ubre.backend.enums.Role;
import com.ubre.backend.enums.UserStatus;
import com.ubre.backend.model.*;
import com.ubre.backend.repository.*;
import com.ubre.backend.service.impl.RideReminderService;
import com.ubre.backend.service.impl.RideServiceImpl;
import com.ubre.backend.websocket.CurrentRideNotification;
import com.ubre.backend.websocket.WebSocketNotificationService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;

public class RideServiceEndRideTest {
    @Mock
    private RideRepository rideRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private DriverRepository driverRepository;
    @Mock
    private WebSocketNotificationService webSocketNotificationService;
    @Mock
    private RideReminderService rideReminderService;
    @Mock
    private PanicRepository panicRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private WaypointRepository waypointRepository;

    @InjectMocks
    private RideServiceImpl rideService;

    private AutoCloseable mocks;

    private final Long VALID_RIDE = 1L;
    private final Long INVALID_RIDE = 2L;
    private final Long COMPLETED_RIDE = 3L;
    private final Long MISSING_RIDE = 4L;

    private final WaypointDto VALID_WAYPOINT = new WaypointDto(null, "Test", 45.26409815558536, 19.8300840078294);

    @BeforeClass
    public void setup() {
        mocks = MockitoAnnotations.openMocks(this);

        User creator = new User() {};
        creator.setId(1L);
        creator.setEmail("creator@ubre.com");
        User passenger = new User() {};
        passenger.setId(4L);
        passenger.setEmail("passenger@ubre.com");

        Driver driver = new Driver(Role.DRIVER, "driver@test.com", "", "", "", "", "", "", UserStatus.ON_RIDE, true, false, "", null, null);
        driver.setId(2L);
        Driver otherDriver = new Driver(Role.DRIVER, "", "", "", "", "", "", "", UserStatus.ON_RIDE, true, false, "", null, null);
        otherDriver.setId(3L);

        List<Waypoint> waypoints = List.of(
                new Waypoint("Test 1", 45.26112038044458, 19.831970846088225, true),
                new Waypoint("Test 2", 45.24989266023353, 19.83244255565896, true),
                new Waypoint("Test 3", 45.249622835580126, 19.81608013093555, true)
        );

        Ride validRide = new Ride(LocalDateTime.now().minusMinutes(20), RideStatus.IN_PROGRESS, creator, driver, waypoints);
        validRide.setId(VALID_RIDE);
        validRide.setDistance(3700.0);
        validRide.setPrice(8.7);
        validRide.setPassengers(List.of(creator, passenger));
        Ride invalidRide = new Ride(LocalDateTime.now().minusMinutes(20), RideStatus.IN_PROGRESS, creator, otherDriver, waypoints);
        invalidRide.setId(INVALID_RIDE);
        Ride completedRide = new Ride(LocalDateTime.now().minusMinutes(40), RideStatus.COMPLETED, creator, driver, waypoints);
        invalidRide.setId(COMPLETED_RIDE);

        when(rideRepository.findById(VALID_RIDE)).thenReturn(Optional.of(validRide));
        when(rideRepository.findById(INVALID_RIDE)).thenReturn(Optional.of(invalidRide));
        when(rideRepository.findById(COMPLETED_RIDE)).thenReturn(Optional.of(completedRide));
        when(rideRepository.findById(MISSING_RIDE)).thenReturn(Optional.empty());

        Authentication auth = Mockito.mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(driver);
        when(auth.getName()).thenReturn(driver.getEmail());
        when(userRepository.findByEmail("driver@test.com")).thenReturn(Optional.of(driver));
        SecurityContext context = Mockito.mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
    }

    @AfterClass
    public void cleanUp() throws Exception {
        if (mocks != null) {
            mocks.close();
        }
        SecurityContextHolder.clearContext();
    }

    @Test()
    public void end_valid_ride() {
        clearInvocations(rideRepository);
        clearInvocations(webSocketNotificationService);
        clearInvocations(emailService);

        Double newPrice = rideService.stopRideInProgress(VALID_RIDE, VALID_WAYPOINT);
        Assert.assertEquals(newPrice, 8.7);

        verify(rideRepository, times(1)).findById(VALID_RIDE);
        verify(rideRepository, times(1)).save(any(Ride.class));
        verify(webSocketNotificationService, atLeastOnce()).sendCurrentRideUpdate(eq(1L), any(CurrentRideNotification.class));
        verify(emailService, times(1)).sendRideCompletedEmail(eq("creator@ubre.com"), any(Ride.class));
        verify(emailService, times(1)).sendRideCompletedEmail(eq("passenger@ubre.com"), any(Ride.class));
    }

    @Test()
    public void end_invalid_ride() {
        ResponseStatusException ex = Assert.expectThrows(ResponseStatusException.class, () -> rideService.stopRideInProgress(INVALID_RIDE, VALID_WAYPOINT));
        Assert.assertEquals(ex.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @Test()
    public void end_missing_ride() {
        ResponseStatusException ex = Assert.expectThrows(ResponseStatusException.class, () -> rideService.stopRideInProgress(MISSING_RIDE, VALID_WAYPOINT));
        Assert.assertEquals(ex.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test()
    public void end_completed_ride() {
        ResponseStatusException ex = Assert.expectThrows(ResponseStatusException.class, () -> rideService.stopRideInProgress(COMPLETED_RIDE, VALID_WAYPOINT));
        Assert.assertEquals(ex.getStatusCode(), HttpStatus.BAD_REQUEST);
    }
}
