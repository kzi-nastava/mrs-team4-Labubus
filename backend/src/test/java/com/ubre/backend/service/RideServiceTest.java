package com.ubre.backend.service;

import com.ubre.backend.dto.RideDto;
import com.ubre.backend.dto.RideOrderDto;
import com.ubre.backend.dto.WaypointDto;
import com.ubre.backend.enums.NotificationType;
import com.ubre.backend.enums.RideStatus;
import com.ubre.backend.enums.Role;
import com.ubre.backend.enums.UserStatus;
import com.ubre.backend.enums.VehicleType;
import com.ubre.backend.model.Driver;
import com.ubre.backend.model.Passenger;
import com.ubre.backend.model.Ride;
import com.ubre.backend.model.User;
import com.ubre.backend.model.UserStats;
import com.ubre.backend.model.Vehicle;
import com.ubre.backend.model.Waypoint;
import com.ubre.backend.repository.DriverRepository;
import com.ubre.backend.repository.PanicRepository;
import com.ubre.backend.repository.RideRepository;
import com.ubre.backend.repository.UserRepository;
import com.ubre.backend.repository.WaypointRepository;
import com.ubre.backend.service.EmailService;
import com.ubre.backend.service.impl.RideReminderService;
import com.ubre.backend.service.impl.RideServiceImpl;
import com.ubre.backend.websocket.CurrentRideNotification;
import com.ubre.backend.websocket.RideAssignmentNotification;
import com.ubre.backend.websocket.WebSocketNotificationService;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RideServiceTest {

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
    private RideServiceImpl service;

    private AutoCloseable mocks;

    @BeforeMethod
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        if (mocks != null) {
            mocks.close();
        }
        SecurityContextHolder.clearContext();
    }

    @Test
    public void orderRide_waypointsNull_badRequest() {
        RideOrderDto dto = makeValidRideOrderDto();
        dto.setWaypoints(null);

        ResponseStatusException ex = Assert.expectThrows(ResponseStatusException.class, () -> service.orderRide(dto));
        Assert.assertEquals(ex.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assert.assertEquals(ex.getReason(), "At least one waypoint is required to order a ride");
    }

    @Test
    public void orderRide_waypointsEmpty_badRequest() {
        RideOrderDto dto = makeValidRideOrderDto();
        dto.setWaypoints(new ArrayList<>());

        ResponseStatusException ex = Assert.expectThrows(ResponseStatusException.class, () -> service.orderRide(dto));
        Assert.assertEquals(ex.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assert.assertEquals(ex.getReason(), "At least one waypoint is required to order a ride");
    }

    @Test
    public void orderRide_waypointsSizeOne_badRequest() {
        RideOrderDto dto = makeValidRideOrderDto();
        dto.setWaypoints(List.of(makeWaypointDto("A", 10.0, 20.0)));

        ResponseStatusException ex = Assert.expectThrows(ResponseStatusException.class, () -> service.orderRide(dto));
        Assert.assertEquals(ex.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assert.assertEquals(ex.getReason(), "At least one waypoint is required to order a ride");
    }

    @Test
    public void orderRide_creatorIdNull_badRequest() {
        RideOrderDto dto = makeValidRideOrderDto();
        dto.setCreatorId(null);

        ResponseStatusException ex = Assert.expectThrows(ResponseStatusException.class, () -> service.orderRide(dto));
        Assert.assertEquals(ex.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assert.assertEquals(ex.getReason(), "Creator id is required to order a ride");
    }

    @Test
    public void orderRide_creatorIdZero_badRequest() {
        RideOrderDto dto = makeValidRideOrderDto();
        dto.setCreatorId(0L);

        ResponseStatusException ex = Assert.expectThrows(ResponseStatusException.class, () -> service.orderRide(dto));
        Assert.assertEquals(ex.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assert.assertEquals(ex.getReason(), "Creator id is required to order a ride");
    }

    @Test
    public void orderRide_noAvailableDrivers_notFound() {
        RideOrderDto dto = makeValidRideOrderDto();
        when(driverRepository.existsDriverWithActiveStatus()).thenReturn(false);

        ResponseStatusException ex = Assert.expectThrows(ResponseStatusException.class, () -> service.orderRide(dto));
        Assert.assertEquals(ex.getStatusCode(), HttpStatus.NOT_FOUND);
        Assert.assertEquals(ex.getReason(), "No available drivers found for the ride");
    }

    @Test
    public void orderRide_allDriversBusy_notFound() {
        RideOrderDto dto = makeValidRideOrderDto();
        when(driverRepository.existsDriverWithActiveStatus()).thenReturn(true);
        when(driverRepository.areAllDriversOnRideWithPendingRides()).thenReturn(true);

        ResponseStatusException ex = Assert.expectThrows(ResponseStatusException.class, () -> service.orderRide(dto));
        Assert.assertEquals(ex.getStatusCode(), HttpStatus.NOT_FOUND);
        Assert.assertEquals(ex.getReason(), "All drivers are currently busy");
    }

    @Test
    public void orderRide_activeDriverEligible_assignsDriver_andRemindersFuture() {
        Passenger creator = makeCreatorUser(10L);
        Driver driver = makeDriverEligible(1L, VehicleType.STANDARD, true, true, 120);

        RideOrderDto dto = makeValidRideOrderDto();
        dto.setCreatorId(creator.getId());
        dto.setVehicleType(VehicleType.STANDARD);
        dto.setBabyFriendly(true);
        dto.setPetFriendly(true);
        LocalDateTime future = LocalDateTime.now().plusMinutes(10).withNano(0);
        dto.setScheduledTime(future.toString());

        when(driverRepository.existsDriverWithActiveStatus()).thenReturn(true);
        when(driverRepository.areAllDriversOnRideWithPendingRides()).thenReturn(false);
        when(driverRepository.findByStatus(UserStatus.ACTIVE)).thenReturn(List.of(driver));
        when(userRepository.findById(creator.getId())).thenReturn(Optional.of(creator));
        stubRideSaveWithId(100L);

        RideDto result = service.orderRide(dto);

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getStatus(), RideStatus.PENDING);
        Assert.assertEquals(result.getDriver().getId(), driver.getId());

        verify(rideRepository, times(1)).save(any(Ride.class));

        ArgumentCaptor<RideAssignmentNotification> assignmentCaptor = ArgumentCaptor.forClass(RideAssignmentNotification.class);
        verify(webSocketNotificationService, times(1)).sendRideAssigned(eq(driver.getId()), assignmentCaptor.capture());
        RideAssignmentNotification notification = assignmentCaptor.getValue();
        Assert.assertEquals(notification.getStatus(), NotificationType.RIDE_ASSIGNED.name());
        Assert.assertNotNull(notification.getRide());
        Assert.assertEquals(notification.getRide().getId(), Long.valueOf(100L));

        verify(rideReminderService, times(1)).start(eq(creator.getId()), eq(future));
        verify(rideReminderService, times(1)).sendCurrentRideUpdate(eq(creator.getId()), any(RideDto.class), eq(future));
        verify(rideReminderService, times(1)).sendCurrentRideUpdate(eq(driver.getId()), any(RideDto.class), eq(future));
    }

    @Test
    public void orderRide_onRideEligible_assignsDriver() {
        Passenger creator = makeCreatorUser(11L);
        Driver activeNotEligible = makeDriverEligible(2L, VehicleType.LUXURY, false, false, 600);
        Driver onRideEligible = makeDriverEligible(3L, VehicleType.STANDARD, false, false, 200);
        Driver onRideToRemove = makeDriverEligible(4L, VehicleType.STANDARD, false, false, 200);

        RideOrderDto dto = makeValidRideOrderDto();
        dto.setCreatorId(creator.getId());
        dto.setVehicleType(VehicleType.STANDARD);

        when(driverRepository.existsDriverWithActiveStatus()).thenReturn(true);
        when(driverRepository.areAllDriversOnRideWithPendingRides()).thenReturn(false);
        when(driverRepository.findByStatus(UserStatus.ACTIVE)).thenReturn(List.of(activeNotEligible));
        when(driverRepository.findByStatus(UserStatus.ON_RIDE)).thenReturn(new ArrayList<>(List.of(onRideEligible, onRideToRemove)));
        when(rideRepository.findByStatus(RideStatus.ACCEPTED)).thenReturn(List.of(makeRideWithDriver(onRideToRemove)));
        when(userRepository.findById(creator.getId())).thenReturn(Optional.of(creator));
        stubRideSaveWithId(200L);

        RideDto result = service.orderRide(dto);

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getDriver().getId(), onRideEligible.getId());
        verify(rideRepository, times(1)).save(any(Ride.class));
    }

    @Test
    public void orderRide_noSuitableDrivers_notFound() {
        Passenger creator = makeCreatorUser(12L);
        Driver activeNotEligible = makeDriverEligible(5L, VehicleType.LUXURY, false, false, 600);
        Driver onRideNotEligible = makeDriverEligible(6L, VehicleType.VAN, false, false, 200);

        RideOrderDto dto = makeValidRideOrderDto();
        dto.setCreatorId(creator.getId());
        dto.setVehicleType(VehicleType.STANDARD);

        when(driverRepository.existsDriverWithActiveStatus()).thenReturn(true);
        when(driverRepository.areAllDriversOnRideWithPendingRides()).thenReturn(false);
        when(driverRepository.findByStatus(UserStatus.ACTIVE)).thenReturn(List.of(activeNotEligible));
        when(driverRepository.findByStatus(UserStatus.ON_RIDE)).thenReturn(List.of(onRideNotEligible));
        when(rideRepository.findByStatus(RideStatus.ACCEPTED)).thenReturn(List.of());

        ResponseStatusException ex = Assert.expectThrows(ResponseStatusException.class, () -> service.orderRide(dto));
        Assert.assertEquals(ex.getStatusCode(), HttpStatus.NOT_FOUND);
        Assert.assertEquals(ex.getReason(), "No suitable drivers found for the ride");
    }

    @Test
    public void orderRide_passengersEmails_addsExistingOnly() {
        Passenger creator = makeCreatorUser(13L);
        Passenger extraPassenger = makeCreatorUser(14L);
        extraPassenger.setEmail("extra@test.com");

        Driver driver = makeDriverEligible(7L, VehicleType.STANDARD, false, false, 100);

        RideOrderDto dto = makeValidRideOrderDto();
        dto.setCreatorId(creator.getId());
        dto.setPassengersEmails(List.of("extra@test.com", "missing@test.com"));

        when(driverRepository.existsDriverWithActiveStatus()).thenReturn(true);
        when(driverRepository.areAllDriversOnRideWithPendingRides()).thenReturn(false);
        when(driverRepository.findByStatus(UserStatus.ACTIVE)).thenReturn(List.of(driver));
        when(userRepository.findById(creator.getId())).thenReturn(Optional.of(creator));
        when(userRepository.findByEmail("extra@test.com")).thenReturn(Optional.of(extraPassenger));
        when(userRepository.findByEmail("missing@test.com")).thenReturn(Optional.empty());
        stubRideSaveWithId(300L);

        service.orderRide(dto);

        ArgumentCaptor<Ride> rideCaptor = ArgumentCaptor.forClass(Ride.class);
        verify(rideRepository).save(rideCaptor.capture());
        Ride saved = rideCaptor.getValue();
        Assert.assertEquals(saved.getPassengers().size(), 2);
        Assert.assertTrue(saved.getPassengers().stream().anyMatch(u -> u.getEmail().equals(creator.getEmail())));
        Assert.assertTrue(saved.getPassengers().stream().anyMatch(u -> u.getEmail().equals(extraPassenger.getEmail())));
    }

    @Test
    public void orderRide_scheduledTimeBlank_usesNowWithNanoZero() {
        Passenger creator = makeCreatorUser(15L);
        Driver driver = makeDriverEligible(8L, VehicleType.STANDARD, false, false, 100);

        RideOrderDto dto = makeValidRideOrderDto();
        dto.setCreatorId(creator.getId());
        dto.setScheduledTime(" ");

        when(driverRepository.existsDriverWithActiveStatus()).thenReturn(true);
        when(driverRepository.areAllDriversOnRideWithPendingRides()).thenReturn(false);
        when(driverRepository.findByStatus(UserStatus.ACTIVE)).thenReturn(List.of(driver));
        when(userRepository.findById(creator.getId())).thenReturn(Optional.of(creator));
        stubRideSaveWithId(400L);

        service.orderRide(dto);

        ArgumentCaptor<Ride> rideCaptor = ArgumentCaptor.forClass(Ride.class);
        verify(rideRepository).save(rideCaptor.capture());
        Ride saved = rideCaptor.getValue();
        Assert.assertNotNull(saved.getStartTime());
        Assert.assertEquals(saved.getStartTime().getNano(), 0);
        long diffSeconds = Duration.between(saved.getStartTime(), saved.getEndTime()).getSeconds();
        Assert.assertEquals(diffSeconds, dto.getRequiredTime().longValue());
    }

    @Test
    public void orderRide_scheduledTimeProvided_parsesAndSetsEndTime() {
        Passenger creator = makeCreatorUser(16L);
        Driver driver = makeDriverEligible(9L, VehicleType.STANDARD, false, false, 100);

        LocalDateTime scheduled = LocalDateTime.of(2026, 2, 10, 15, 30, 0);

        RideOrderDto dto = makeValidRideOrderDto();
        dto.setCreatorId(creator.getId());
        dto.setScheduledTime(scheduled.toString());
        dto.setRequiredTime(900.0);

        when(driverRepository.existsDriverWithActiveStatus()).thenReturn(true);
        when(driverRepository.areAllDriversOnRideWithPendingRides()).thenReturn(false);
        when(driverRepository.findByStatus(UserStatus.ACTIVE)).thenReturn(List.of(driver));
        when(userRepository.findById(creator.getId())).thenReturn(Optional.of(creator));
        stubRideSaveWithId(500L);

        service.orderRide(dto);

        ArgumentCaptor<Ride> rideCaptor = ArgumentCaptor.forClass(Ride.class);
        verify(rideRepository).save(rideCaptor.capture());
        Ride saved = rideCaptor.getValue();
        Assert.assertEquals(saved.getStartTime(), scheduled);
        Assert.assertEquals(saved.getEndTime(), scheduled.plusSeconds(dto.getRequiredTime().longValue()));
    }

    @Test
    public void orderRide_startTimePast_noReminderStart() {
        Passenger creator = makeCreatorUser(17L);
        Driver driver = makeDriverEligible(10L, VehicleType.STANDARD, false, false, 100);

        RideOrderDto dto = makeValidRideOrderDto();
        dto.setCreatorId(creator.getId());
        dto.setScheduledTime(LocalDateTime.now().minusMinutes(5).withNano(0).toString());

        when(driverRepository.existsDriverWithActiveStatus()).thenReturn(true);
        when(driverRepository.areAllDriversOnRideWithPendingRides()).thenReturn(false);
        when(driverRepository.findByStatus(UserStatus.ACTIVE)).thenReturn(List.of(driver));
        when(userRepository.findById(creator.getId())).thenReturn(Optional.of(creator));
        stubRideSaveWithId(600L);

        service.orderRide(dto);

        verify(rideReminderService, never()).start(anyLong(), any(LocalDateTime.class));
    }

    @Test
    public void orderRide_waypointsMapped() {
        Passenger creator = makeCreatorUser(18L);
        Driver driver = makeDriverEligible(11L, VehicleType.STANDARD, false, false, 100);

        WaypointDto wp1 = makeWaypointDto("Start", 45.0, 19.0);
        WaypointDto wp2 = makeWaypointDto("End", 46.0, 20.0);

        RideOrderDto dto = makeValidRideOrderDto();
        dto.setCreatorId(creator.getId());
        dto.setWaypoints(List.of(wp1, wp2));

        when(driverRepository.existsDriverWithActiveStatus()).thenReturn(true);
        when(driverRepository.areAllDriversOnRideWithPendingRides()).thenReturn(false);
        when(driverRepository.findByStatus(UserStatus.ACTIVE)).thenReturn(List.of(driver));
        when(userRepository.findById(creator.getId())).thenReturn(Optional.of(creator));
        stubRideSaveWithId(700L);

        service.orderRide(dto);

        ArgumentCaptor<Ride> rideCaptor = ArgumentCaptor.forClass(Ride.class);
        verify(rideRepository).save(rideCaptor.capture());
        Ride saved = rideCaptor.getValue();
        Assert.assertEquals(saved.getWaypoints().size(), 2);
        Assert.assertEquals(saved.getWaypoints().get(0).getLabel(), "Start");
        Assert.assertEquals(saved.getWaypoints().get(0).getLatitude(), Double.valueOf(45.0));
        Assert.assertEquals(saved.getWaypoints().get(0).getLongitude(), Double.valueOf(19.0));
        Assert.assertEquals(saved.getWaypoints().get(1).getLabel(), "End");
    }

    @Test
    public void startRide_setsInProgressAndNotifies() {
        Driver driver = makeDriverEligible(20L, VehicleType.STANDARD, false, false, 50);
        Passenger creator = makeCreatorUser(21L);

        Ride ride = makeRideForStartAndEnd(1L, creator, driver, RideStatus.PENDING);
        when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
        when(rideRepository.save(any(Ride.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.startRide(1L);

        Assert.assertEquals(ride.getStatus(), RideStatus.IN_PROGRESS);
        Assert.assertEquals(driver.getStatus(), UserStatus.ON_RIDE);
        verify(webSocketNotificationService, times(1)).sendCurrentRideUpdate(eq(creator.getId()), any(CurrentRideNotification.class));
    }

    @Test
    public void endRide_completesRide_andSendsEmails() {
        Driver driver = makeDriverEligible(30L, VehicleType.STANDARD, false, false, 50);
        Passenger creator = makeCreatorUser(31L);
        Passenger passenger = makeCreatorUser(32L);
        passenger.setEmail("p2@test.com");

        Ride ride = makeRideForStartAndEnd(2L, creator, driver, RideStatus.IN_PROGRESS);
        ride.setPassengers(List.of(creator, passenger));

        setAuthenticatedUser(driver);

        when(rideRepository.findById(2L)).thenReturn(Optional.of(ride));
        when(rideRepository.save(any(Ride.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RideDto result = service.endRide(2L);

        Assert.assertEquals(result.getStatus(), RideStatus.COMPLETED);
        Assert.assertEquals(ride.getDriver().getStatus(), UserStatus.ACTIVE);
        verify(webSocketNotificationService, times(1)).sendCurrentRideUpdate(eq(creator.getId()), any(CurrentRideNotification.class));
        verify(emailService, times(2)).sendRideCompletedEmail(any(String.class), eq(ride));
    }

    @Test
    public void endRide_notInProgress_badRequest() {
        Driver driver = makeDriverEligible(40L, VehicleType.STANDARD, false, false, 50);
        Passenger creator = makeCreatorUser(41L);

        Ride ride = makeRideForStartAndEnd(3L, creator, driver, RideStatus.PENDING);

        setAuthenticatedUser(driver);

        when(rideRepository.findById(3L)).thenReturn(Optional.of(ride));

        ResponseStatusException ex = Assert.expectThrows(ResponseStatusException.class, () -> service.endRide(3L));
        Assert.assertEquals(ex.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assert.assertEquals(ex.getReason(), "Ride is not in progress");
    }

    @Test
    public void stopRideInProgress_allWaypointsVisited_setsZeroPriceDistance() {
        Driver driver = makeDriverEligible(50L, VehicleType.STANDARD, false, false, 50);
        Passenger creator = makeCreatorUser(51L);

        Ride ride = makeRideForStartAndEnd(4L, creator, driver, RideStatus.IN_PROGRESS);
        ride.setPassengers(List.of(creator));
        ride.setWaypoints(List.of(
                new Waypoint("A", 1.0, 2.0, true),
                new Waypoint("B", 3.0, 4.0, true)
        ));
        ride.setPrice(123.0);
        ride.setDistance(456.0);

        setAuthenticatedUser(driver);

        when(rideRepository.findById(4L)).thenReturn(Optional.of(ride));
        when(rideRepository.save(any(Ride.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Double resultPrice = service.stopRideInProgress(4L, makeWaypointDto("End", 5.0, 6.0));

        Assert.assertEquals(resultPrice, Double.valueOf(0.0));
        Assert.assertEquals(ride.getStatus(), RideStatus.COMPLETED);
        Assert.assertEquals(ride.getDriver().getStatus(), UserStatus.ACTIVE);
        Assert.assertEquals(ride.getPrice(), Double.valueOf(0.0));
        Assert.assertEquals(ride.getDistance(), Double.valueOf(0.0));
        verify(webSocketNotificationService, times(1)).sendCurrentRideUpdate(eq(creator.getId()), any(CurrentRideNotification.class));
        verify(emailService, times(1)).sendRideCompletedEmail(eq(creator.getEmail()), eq(ride));
    }

    private RideOrderDto makeValidRideOrderDto() {
        List<WaypointDto> waypoints = List.of(
                makeWaypointDto("Start", 45.0, 19.0),
                makeWaypointDto("End", 46.0, 20.0)
        );
        RideOrderDto dto = new RideOrderDto();
        dto.setId(1L);
        dto.setCreatorId(1L);
        dto.setPassengersEmails(new ArrayList<>());
        dto.setWaypoints(waypoints);
        dto.setVehicleType(VehicleType.STANDARD);
        dto.setBabyFriendly(false);
        dto.setPetFriendly(false);
        dto.setScheduledTime(null);
        dto.setDistance(1000.0);
        dto.setRequiredTime(600.0);
        dto.setPrice(500.0);
        return dto;
    }

    private WaypointDto makeWaypointDto(String label, double lat, double lon) {
        return new WaypointDto(null, label, lat, lon);
    }

    private Passenger makeCreatorUser(Long id) {
        Passenger user = new Passenger();
        user.setId(id);
        user.setRole(Role.REGISTERED_USER);
        user.setEmail("creator" + id + "@test.com");
        user.setName("Creator");
        user.setSurname("User");
        user.setPhone("123");
        user.setAddress("Address");
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }

    private Driver makeDriverEligible(Long id, VehicleType vehicleType, boolean babyFriendly, boolean petFriendly, int activeMinutes) {
        Driver driver = new Driver();
        driver.setId(id);
        driver.setRole(Role.DRIVER);
        driver.setEmail("driver" + id + "@test.com");
        driver.setName("Driver");
        driver.setSurname("User");
        driver.setPhone("456");
        driver.setAddress("Address");
        driver.setStatus(UserStatus.ACTIVE);

        Vehicle vehicle = new Vehicle();
        vehicle.setType(vehicleType);
        vehicle.setBabyFriendly(babyFriendly);
        vehicle.setPetFriendly(petFriendly);
        driver.setVehicle(vehicle);

        UserStats stats = new UserStats();
        stats.setActivePast24Hours(activeMinutes);
        driver.setStats(stats);
        return driver;
    }

    private Ride makeRideWithDriver(Driver driver) {
        Ride ride = new Ride();
        ride.setDriver(driver);
        return ride;
    }

    private Ride makeRideForStartAndEnd(Long id, User creator, Driver driver, RideStatus status) {
        Ride ride = new Ride();
        ride.setId(id);
        ride.setCreator(creator);
        ride.setDriver(driver);
        ride.setStatus(status);
        ride.setStartTime(LocalDateTime.now().minusMinutes(5));
        ride.setEndTime(LocalDateTime.now().plusMinutes(10));
        ride.setWaypoints(List.of(new Waypoint("A", 1.0, 2.0)));
        ride.setPassengers(List.of(creator));
        ride.setPrice(100.0);
        ride.setDistance(1000.0);
        return ride;
    }

    private void stubRideSaveWithId(Long id) {
        when(rideRepository.save(any(Ride.class))).thenAnswer(invocation -> {
            Ride ride = invocation.getArgument(0);
            if (ride.getId() == null) {
                ride.setId(id);
            }
            return ride;
        });
    }

    private void setAuthenticatedUser(User user) {
        Authentication auth = Mockito.mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);
        SecurityContext context = Mockito.mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
    }
}
