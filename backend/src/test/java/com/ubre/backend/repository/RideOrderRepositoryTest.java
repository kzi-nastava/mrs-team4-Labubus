package com.ubre.backend.repository;

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
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RideOrderRepositoryTest extends AbstractTestNGSpringContextTests {

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

    @BeforeMethod
    public void resetDatabase() {
        cleanupDatabase();
    }

    @Test
    public void existsDriverWithActiveStatus_false_whenNoActiveOrOnRideDrivers() {
        createDriverWithVehicleAndStats("inactive1@test.com", UserStatus.INACTIVE, VehicleType.STANDARD, false, false, 120);

        Boolean result = driverRepository.existsDriverWithActiveStatus();

        Assert.assertFalse(result);
    }

    @Test
    public void existsDriverWithActiveStatus_true_evenIfActiveDriverNotEligible() {
        createDriverWithVehicleAndStats("active1@test.com", UserStatus.ACTIVE, VehicleType.LUXURY, false, false, 600);

        Boolean result = driverRepository.existsDriverWithActiveStatus();

        Assert.assertTrue(result);
    }

    @Test
    public void existsDriverWithActiveStatus_true_whenOnRideDriverExists() {
        createDriverWithVehicleAndStats("onride1@test.com", UserStatus.ON_RIDE, VehicleType.STANDARD, true, true, 100);

        Boolean result = driverRepository.existsDriverWithActiveStatus();

        Assert.assertTrue(result);
    }

    @Test
    public void areAllDriversOnRideWithPendingRides_true_whenAllOnRideAndHavePending() {
        Driver d1 = createDriverWithVehicleAndStats("d1@test.com", UserStatus.ON_RIDE, VehicleType.STANDARD, false, false, 100);
        Driver d2 = createDriverWithVehicleAndStats("d2@test.com", UserStatus.ON_RIDE, VehicleType.STANDARD, false, false, 200);
        Passenger creator = createPassenger("creator@test.com");

        createRide(creator, d1, RideStatus.PENDING);
        createRide(creator, d2, RideStatus.PENDING);

        Boolean result = driverRepository.areAllDriversOnRideWithPendingRides();

        Assert.assertTrue(result);
    }

    @Test
    public void areAllDriversOnRideWithPendingRides_false_whenAnyDriverNotOnRide() {
        Driver onRide = createDriverWithVehicleAndStats("d3@test.com", UserStatus.ON_RIDE, VehicleType.STANDARD, false, false, 120);
        createDriverWithVehicleAndStats("d4@test.com", UserStatus.ACTIVE, VehicleType.STANDARD, false, false, 120);
        Passenger creator = createPassenger("creator2@test.com");

        createRide(creator, onRide, RideStatus.PENDING);

        Boolean result = driverRepository.areAllDriversOnRideWithPendingRides();

        Assert.assertFalse(result);
    }

    @Test
    public void areAllDriversOnRideWithPendingRides_false_whenOnRideDriverMissingPendingRide() {
        Driver d1 = createDriverWithVehicleAndStats("d5@test.com", UserStatus.ON_RIDE, VehicleType.STANDARD, false, false, 120);
        Driver d2 = createDriverWithVehicleAndStats("d6@test.com", UserStatus.ON_RIDE, VehicleType.STANDARD, false, false, 120);
        Passenger creator = createPassenger("creator3@test.com");

        createRide(creator, d1, RideStatus.PENDING);

        Boolean result = driverRepository.areAllDriversOnRideWithPendingRides();

        Assert.assertFalse(result);
    }

    @Test
    public void areAllDriversOnRideWithPendingRides_false_whenOnlyAcceptedRidesExist() {
        Driver d1 = createDriverWithVehicleAndStats("d7@test.com", UserStatus.ON_RIDE, VehicleType.STANDARD, false, false, 120);
        Driver d2 = createDriverWithVehicleAndStats("d8@test.com", UserStatus.ON_RIDE, VehicleType.STANDARD, false, false, 120);
        Passenger creator = createPassenger("creator4@test.com");

        createRide(creator, d1, RideStatus.ACCEPTED);
        createRide(creator, d2, RideStatus.ACCEPTED);

        Boolean result = driverRepository.areAllDriversOnRideWithPendingRides();

        Assert.assertFalse(result);
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

    private Ride createRide(User creator, Driver driver, RideStatus status) {
        Ride ride = new Ride();
        ride.setCreator(creator);
        ride.setDriver(driver);
        ride.setStatus(status);
        ride.setStartTime(LocalDateTime.now().withNano(0));
        ride.setEndTime(LocalDateTime.now().plusMinutes(10).withNano(0));
        ride.setWaypoints(List.of(new Waypoint("A", 45.0, 19.0), new Waypoint("B", 46.0, 20.0)));
        ride.setPassengers(List.of(creator));
        ride.setDistance(1200.0);
        ride.setPrice(500.0);
        ride.setPanic(false);
        ride.setFavorite(false);
        return rideRepository.save(ride);
    }
}
