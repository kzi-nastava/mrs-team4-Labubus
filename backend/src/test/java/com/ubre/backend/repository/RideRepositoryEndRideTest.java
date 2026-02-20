package com.ubre.backend.repository;

import com.ubre.backend.enums.RideStatus;
import com.ubre.backend.enums.Role;
import com.ubre.backend.enums.UserStatus;
import com.ubre.backend.enums.VehicleType;
import com.ubre.backend.model.Driver;
import com.ubre.backend.model.Passenger;
import com.ubre.backend.model.Ride;
import com.ubre.backend.model.User;
import com.ubre.backend.model.Vehicle;
import com.ubre.backend.model.Waypoint;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RideRepositoryEndRideTest extends AbstractTestNGSpringContextTests {
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private RideRepository rideRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private WaypointRepository waypointRepository;
    @Autowired
    private EntityManager entityManager;

    @AfterMethod
    public void cleanup() {
        cleanupDatabase();
    }

    @Test
    public void find_drivers_current_ride() {
        Driver DRIVER_ON_A_RIDE = createDriverWithVehicle("driver@test.com", UserStatus.ON_RIDE);
        User USER_ON_A_RIDE = createPassenger("passenger@test.com");
        Ride RIDE_IN_PROGRESS = createRide(USER_ON_A_RIDE, DRIVER_ON_A_RIDE, RideStatus.IN_PROGRESS, LocalDateTime.now().minusMinutes(5), List.of(USER_ON_A_RIDE));

        Optional<Ride> ride = rideRepository.findFirstByDriverAndStatusOrderByStartTimeAsc(DRIVER_ON_A_RIDE, RideStatus.IN_PROGRESS);
        Assert.assertFalse(ride.isEmpty());
        Assert.assertEquals(ride.get().getId(), RIDE_IN_PROGRESS.getId());
    }

    @Test
    public void find_users_current_ride() {
        Driver DRIVER_ON_A_RIDE = createDriverWithVehicle("driver@test.com", UserStatus.ON_RIDE);
        User USER_ON_A_RIDE = createPassenger("passenger@test.com");
        Ride RIDE_IN_PROGRESS = createRide(USER_ON_A_RIDE, DRIVER_ON_A_RIDE, RideStatus.IN_PROGRESS, LocalDateTime.now().minusMinutes(5), List.of(USER_ON_A_RIDE));

        Optional<Ride> ride = rideRepository.findFirstByCreatorAndStatusOrderByStartTimeAsc(USER_ON_A_RIDE, RideStatus.IN_PROGRESS);
        Assert.assertFalse(ride.isEmpty());
        Assert.assertEquals(ride.get().getId(), RIDE_IN_PROGRESS.getId());
    }

    @Test
    public void find_drivers_scheduled_ride() {
        Driver DRIVER_WITH_SCHEDULED_RIDE = createDriverWithVehicle("driver@test.com", UserStatus.ON_RIDE);
        User USER_WITH_SCHEDULED_RIDE = createPassenger("passenger@test.com");
        Ride SCHEDULED_RIDE = createRide(USER_WITH_SCHEDULED_RIDE, DRIVER_WITH_SCHEDULED_RIDE, RideStatus.PENDING, LocalDateTime.now().plusMinutes(30), List.of(USER_WITH_SCHEDULED_RIDE));

        Optional<Ride> ride = rideRepository.findFirstByDriverAndStatusAndStartTimeBeforeOrderByStartTimeAsc(DRIVER_WITH_SCHEDULED_RIDE, RideStatus.PENDING, LocalDateTime.now());
        Assert.assertTrue(ride.isEmpty());
    }

    @Test
    public void find_users_scheduled_ride() {
        Driver DRIVER_WITH_SCHEDULED_RIDE = createDriverWithVehicle("driver@test.com", UserStatus.ON_RIDE);
        User USER_WITH_SCHEDULED_RIDE = createPassenger("passenger@test.com");
        Ride SCHEDULED_RIDE = createRide(USER_WITH_SCHEDULED_RIDE, DRIVER_WITH_SCHEDULED_RIDE, RideStatus.PENDING, LocalDateTime.now().plusMinutes(30), List.of(USER_WITH_SCHEDULED_RIDE));

        Optional<Ride> ride = rideRepository.findFirstByCreatorAndStatusAndStartTimeBeforeOrderByStartTimeAsc(USER_WITH_SCHEDULED_RIDE, RideStatus.PENDING, LocalDateTime.now());
        Assert.assertTrue(ride.isEmpty());
    }

    @Test
    public void find_drivers_pending_ride() {
        Driver DRIVER_WITH_PENDING_RIDE = createDriverWithVehicle("driver@test.com", UserStatus.ACTIVE);
        User USER_WITH_PENDING_RIDE = createPassenger("passengere@test.com");
        Ride PENDING_RIDE = createRide(USER_WITH_PENDING_RIDE, DRIVER_WITH_PENDING_RIDE, RideStatus.PENDING, LocalDateTime.now().minusMinutes(5), List.of(USER_WITH_PENDING_RIDE));

        Optional<Ride> ride = rideRepository.findFirstByDriverAndStatusAndStartTimeBeforeOrderByStartTimeAsc(DRIVER_WITH_PENDING_RIDE, RideStatus.PENDING, LocalDateTime.now());
        Assert.assertFalse(ride.isEmpty());
        Assert.assertEquals(ride.get().getId(), PENDING_RIDE.getId());
    }

    @Test
    public void find_users_pending_ride() {
        Driver DRIVER_WITH_PENDING_RIDE = createDriverWithVehicle("driver@test.com", UserStatus.ACTIVE);
        User USER_WITH_PENDING_RIDE = createPassenger("passenger@test.com");
        Ride PENDING_RIDE = createRide(USER_WITH_PENDING_RIDE, DRIVER_WITH_PENDING_RIDE, RideStatus.PENDING, LocalDateTime.now().minusMinutes(5), List.of(USER_WITH_PENDING_RIDE));

        Optional<Ride> ride = rideRepository.findFirstByCreatorAndStatusAndStartTimeBeforeOrderByStartTimeAsc(USER_WITH_PENDING_RIDE, RideStatus.PENDING, LocalDateTime.now());
        Assert.assertFalse(ride.isEmpty());
        Assert.assertEquals(ride.get().getId(), PENDING_RIDE.getId());
    }

    @Test
    public void find_drivers_missing_ride() {
        Driver DRIVER_WITH_NO_RIDE = createDriverWithVehicle("driver@test.com", UserStatus.ACTIVE);

        Optional<Ride> currentRide = rideRepository.findFirstByDriverAndStatusOrderByStartTimeAsc(DRIVER_WITH_NO_RIDE, RideStatus.IN_PROGRESS);
        Assert.assertTrue(currentRide.isEmpty());

        Optional<Ride> pendingRide = rideRepository.findFirstByDriverAndStatusAndStartTimeBeforeOrderByStartTimeAsc(DRIVER_WITH_NO_RIDE, RideStatus.PENDING, LocalDateTime.now());
        Assert.assertTrue(pendingRide.isEmpty());
    }

    @Test
    public void find_users_missing_ride() {
        User USER_WITH_NO_RIDE = createPassenger("passenger@test.com");

        Optional<Ride> currentRide = rideRepository.findFirstByCreatorAndStatusOrderByStartTimeAsc(USER_WITH_NO_RIDE, RideStatus.IN_PROGRESS);
        Assert.assertTrue(currentRide.isEmpty());

        Optional<Ride> pendingRide = rideRepository.findFirstByCreatorAndStatusAndStartTimeBeforeOrderByStartTimeAsc(USER_WITH_NO_RIDE, RideStatus.PENDING, LocalDateTime.now());
        Assert.assertTrue(pendingRide.isEmpty());
    }

    @Test
    public void find_passengers_current_ride() {
        Driver DRIVER_ON_A_RIDE = createDriverWithVehicle("driver@test.com", UserStatus.ON_RIDE);
        User USER_ON_A_RIDE = createPassenger("passenger@test.com");
        User PASSENGER = createPassenger("passenger2@test.com");
        Ride RIDE_IN_PROGRESS = createRide(USER_ON_A_RIDE, DRIVER_ON_A_RIDE, RideStatus.IN_PROGRESS, LocalDateTime.now().minusMinutes(5), List.of(USER_ON_A_RIDE, PASSENGER));

        Optional<Ride> ride = rideRepository.findFirstByPassengersIdAndStatusOrderByStartTimeAsc(PASSENGER.getId(), RideStatus.IN_PROGRESS);
        Assert.assertFalse(ride.isEmpty());
        Assert.assertEquals(ride.get().getId(), RIDE_IN_PROGRESS.getId());
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

    private Ride createRide(User creator, Driver driver, RideStatus status, LocalDateTime startTime, List<User> passengers) {
        Ride ride = new Ride();
        ride.setCreator(creator);
        ride.setDriver(driver);
        ride.setStatus(status);
        ride.setStartTime(startTime);
        ride.setEndTime(null);
        ride.setWaypoints(List.of(new Waypoint("A", 45.0, 19.0), new Waypoint("B", 46.0, 20.0)));
        ride.setPassengers(passengers);
        ride.setDistance(1200.0);
        ride.setPrice(500.0);
        ride.setPanic(false);
        ride.setFavorite(false);
        return rideRepository.save(ride);
    }
}
