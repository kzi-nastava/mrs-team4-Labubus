package com.ubre.backend.service.impl;

import com.ubre.backend.dto.*;
import com.ubre.backend.enums.NotificationType;
import com.ubre.backend.enums.RideStatus;
import com.ubre.backend.enums.Role;
import com.ubre.backend.enums.UserStatus;
import com.ubre.backend.model.*;
import com.ubre.backend.repository.DriverRepository;
import com.ubre.backend.repository.PanicRepository;
import com.ubre.backend.repository.RideRepository;
import com.ubre.backend.repository.UserRepository;
import com.ubre.backend.service.EmailService;
import com.ubre.backend.service.RideService;
import com.ubre.backend.websocket.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class RideServiceImpl implements RideService {
    @Autowired
    private RideRepository rideRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private WebSocketNotificationService webSocketNotificationService;
    @Autowired
    private RideReminderService rideReminderService;
    @Autowired
    private PanicRepository panicRepository;
    @Autowired
    private EmailService emailService;

    // Mock data for rides
    List<RideDto> rides = new ArrayList<RideDto>();

    @Override
    public RideDto createRide(Long userId, RideDto rideDto) {
        // postavljanje id-ja vožnje, može se koristiti neki generator id-jeva ili inkrementalni brojač
        // provera da li je korisnik već na nekoj vožnji, onda ne može
        UserDto foundUser = null;
        if (foundUser.getStatus() == UserStatus.ON_RIDE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already on a ride");
        }
        rideDto.setId((long) (rides.size() + 1));
        rides.add(rideDto);
        return rideDto; // i druge informacije po potrebi
    }

    @Override
    public RideDto getRideById(Long id) {
        Optional<Ride> ride = rideRepository.findById(id);
        if (ride.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found");

        // neophodno je proveriti takodje da li je ride prihvaćen, onda može da se startuje
        // tada vraćamo kod 400
        return new RideDto(ride.get());
    }

    @Override
    public List<RideDto> getUserRides(Long userId) {
        return List.of();
    }

    @Override
    public List<RideDto> getDriverRides(Long driverId) {
        return List.of();
    }

    @Override
    public void acceptRide(Long rideId, Long driverId) {

    }

    @Override
    public void rejectRide(Long rideId, String reason) {

    }

    @Override
    public void startRide(Long rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found"));
        ride.setStatus(RideStatus.IN_PROGRESS);
        ride.getDriver().setStatus(UserStatus.ON_RIDE);
        rideRepository.save(ride);
        webSocketNotificationService.sendCurrentRideUpdate(ride.getCreator().getId(), new CurrentRideNotification(
                NotificationType.RIDE_STARTED.name(),
                new RideDto(ride)
        ));
    }

    @Override
    public RideDto endRide(Long rideId) {
        Ride ride = this.getRideOrThrow(rideId);

        this.checkRidePrivilege(ride.getDriver().getId());

        if (ride.getStatus() != RideStatus.IN_PROGRESS)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ride is not in progress");

        ride.setEndTime(LocalDateTime.now());
        ride.setStatus(RideStatus.COMPLETED);
        ride.getDriver().setStatus(UserStatus.ACTIVE);

        CurrentRideNotification currentRideNotification = new CurrentRideNotification(
                NotificationType.RIDE_COMPLETED.name(),
                null
        );

        this.webSocketNotificationService.sendCurrentRideUpdate(ride.getCreator().getId(), currentRideNotification);
        ride = rideRepository.save(ride);
        for (User user : ride.getPassengers())
            emailService.sendRideCompletedEmail(user.getEmail(), ride);
        return new RideDto(ride);
    }

    @Override
    public Double stopRideInProgress(Long rideId, WaypointDto waypoint) {
        Ride ride = this.getRideOrThrow(rideId);

        this.checkRidePrivilege(ride.getDriver().getId());

        if (ride.getStatus() != RideStatus.IN_PROGRESS)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ride is not in progress");

        Waypoint endLocation = new Waypoint(
                waypoint.getLabel(),
                waypoint.getLatitude(),
                waypoint.getLongitude()
        );

        ride.setEndTime(LocalDateTime.now());
        ride.getWaypoints().add(endLocation);
        ride.setStatus(RideStatus.COMPLETED);
        ride.getDriver().setStatus(UserStatus.ACTIVE);

        CurrentRideNotification currentRideNotification = new CurrentRideNotification(
                NotificationType.RIDE_COMPLETED.name(),
                null
        );

        Double newPrice = this.estimateNewPrice(ride);
        this.webSocketNotificationService.sendCurrentRideUpdate(ride.getCreator().getId(), currentRideNotification);
        for (User user : ride.getPassengers())
            emailService.sendRideCompletedEmail(user.getEmail(), ride);
        return newPrice;
    }

    private double estimateNewPrice(Ride ride) {
        LocalDateTime start = ride.getStartTime();
        LocalDateTime end = LocalDateTime.now();

        long elapsedMinutes = Duration.between(start, end).toMinutes();

        long standardMinutes = 20;

        double factor = (double) elapsedMinutes / standardMinutes;

        factor = Math.max(0.5, Math.min(factor, 1.0));

        double price = ride.getPrice() * factor;

        double finalPrice = Math.round(price * 100.0) / 100.0;

        ride.setPrice(finalPrice);
        rideRepository.save(ride);

        return finalPrice;
    }


    @Override
    public double estimateRidePrice(RideDto rideDto) {
        return 0;
    }

    @Override
    public List<RideDto> getRidesBetween(LocalDateTime start, LocalDateTime end) {
        return List.of();
    }

    @Override
    public List<RideCardDto> getFavoriteRides(Long userId, Integer skip, Integer count, RideQueryDto query) {
        Optional<User> creator = userRepository.findById(userId);
        if (creator.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        Pageable pageable = parseToPageable(skip, count, query);

        if (query != null && query.getDate() != null)
            return rideRepository.findByCreatorAndFavoriteTrueAndStartTimeBetween(creator.get(), query.getDate(), query.getDate().plusDays(1), pageable).stream().map(RideCardDto::new).toList();
        return rideRepository.findByCreatorAndFavoriteTrue(creator.get(), pageable).stream().map(RideCardDto::new).toList();
    }

    @Override
    public void addRideToFavorites(Long userId, Long rideId) {
        Optional<Ride> rideOptional = rideRepository.findById(rideId);
        if (rideOptional.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found");

        Ride ride = rideOptional.get();
        if (!Objects.equals(ride.getCreator().getId(), userId))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Users may only favorite their rides");

        ride.setFavorite(true);
        rideRepository.save(ride);
    }

    @Override
    public void removeRideFromFavorites(Long userId, Long rideId) {
        Optional<Ride> rideOptional = rideRepository.findById(rideId);
        if (rideOptional.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found");

        Ride ride = rideOptional.get();
        if (!Objects.equals(ride.getCreator().getId(), userId))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Users may only unfavorite their rides");

        ride.setFavorite(false);
        rideRepository.save(ride);
    }

    @Override
    public List<UserDto> getAvailableDrivers(RideDto rideDto) {
        // do a scan by availabilty and a suitable vehicle
        // if ride does not exist
        boolean rideExists = true;
        if (!rideExists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found");
        }

        boolean driversAvailable = false;
        if (!driversAvailable) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No available drivers found for the ride");
        }

        boolean allBusy = false;
        if (allBusy) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "All drivers are currently busy");
        }
        return List.of();
    }



    @Override
    public RideDto scheduleRide(Long userId, RideDto rideDto) {
        RideDto mock = new RideDto();
        return mock;
    }

    public List<RideCardDto> getMyRideHistory(Long userId, Integer skip, Integer count, RideQueryDto query) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        Pageable pageable = parseToPageable(skip, count, query);

        if (user.get().getRole() == Role.DRIVER) {
            Driver driver = driverRepository.findById(userId).get();
            if (query != null && query.getDate() != null)
                return rideRepository.findByDriverAndStatusInAndStartTimeBetween(driver, List.of(RideStatus.COMPLETED, RideStatus.CANCELLED), query.getDate(), query.getDate().plusDays(1), pageable).stream().map(RideCardDto::new).toList();
            return rideRepository.findByDriverAndStatusIn(driver, List.of(RideStatus.COMPLETED, RideStatus.CANCELLED), pageable).stream().map(RideCardDto::new).toList();
        }
        if (query != null && query.getDate() != null)
            return rideRepository.findByCreatorAndStatusInAndStartTimeBetween(user.get(), List.of(RideStatus.COMPLETED, RideStatus.CANCELLED), query.getDate(), query.getDate().plusDays(1), pageable).stream().map(RideCardDto::new).toList();
        return rideRepository.findByCreatorAndStatusIn(user.get(), List.of(RideStatus.COMPLETED, RideStatus.CANCELLED), pageable).stream().map(RideCardDto::new).toList();
    }

    public List<RideCardDto> getRideHistory(Integer skip, Integer count, RideQueryDto query) {
        Pageable pageable = parseToPageable(skip, count, query);

        if (query != null && query.getDate() != null)
            return rideRepository.findByStatusInAndStartTimeBetween(List.of(RideStatus.COMPLETED, RideStatus.CANCELLED), query.getDate(), query.getDate().plusDays(1), pageable).stream().map(RideCardDto::new).toList();
        return rideRepository.findByStatusIn(List.of(RideStatus.COMPLETED, RideStatus.CANCELLED), pageable).stream().map(RideCardDto::new).toList();
    }

    @Override
    public List<RideCardDto> getScheduledRides(Long driverId, Integer skip, Integer count, RideQueryDto query) {
        Optional<Driver> driver = driverRepository.findById(driverId);
        if (driver.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found");

        Pageable pageable = parseToPageable(skip, count, query);

        if (query != null && query.getDate() != null)
            return rideRepository.findByDriverAndStatusInAndStartTimeBetween(driver.get(), List.of(RideStatus.PENDING), query.getDate(), query.getDate().plusDays(1), pageable).stream().map(RideCardDto::new).toList();
        return rideRepository.findByDriverAndStatusIn(driver.get(), List.of(RideStatus.PENDING), pageable).stream().map(RideCardDto::new).toList();
    }

    @Override
    public RideDto getCurrentRide() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to get current ride for unauthenticated user");

        User jwtUser = (User) auth.getPrincipal();
        if (jwtUser == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to get current ride for unauthenticated user");

        if (jwtUser.getRole() == Role.DRIVER) {
            Optional<Driver> driver = driverRepository.findById(jwtUser.getId());
            if (driver.isEmpty())
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown user");

            Optional<Ride> ride = rideRepository.findFirstByDriverAndStatusOrderByStartTimeAsc(driver.get(), RideStatus.IN_PROGRESS);
            if (ride.isPresent())
                return new RideDto(ride.get());

            ride = rideRepository.findFirstByDriverAndStatusAndStartTimeBeforeOrderByStartTimeAsc(driver.get(), RideStatus.PENDING, LocalDateTime.now().plusMinutes(1));
            return ride.map(RideDto::new).orElse(null);
        }
        else {
            Optional<User> user = userRepository.findById(jwtUser.getId());
            if (user.isEmpty())
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown user");

            Optional<Ride> ride = rideRepository.findFirstByCreatorAndStatusOrderByStartTimeAsc(user.get(), RideStatus.IN_PROGRESS);
            if (ride.isPresent())
                return new RideDto(ride.get());

            ride = rideRepository.findFirstByCreatorAndStatusAndStartTimeBeforeOrderByStartTimeAsc(user.get(), RideStatus.PENDING, LocalDateTime.now().plusMinutes(1));
            return ride.map(RideDto::new).orElse(null);
        }
    }

    @Override
    public void trackRide(Long id) {

    }

    private Pageable parseToPageable(Integer skip, Integer count, RideQueryDto query) {
        Sort sort = Sort.by("startTime").descending();
        if (query != null && query.getSortBy() != null) {
            sort = Sort.by(query.getSortBy());
            if (query.getAscending())
                sort = sort.ascending();
            else
                sort = sort.descending();
        }

        Pageable pageable = PageRequest.of(0, 10, sort);
        if (skip != null && skip > -1 && count != null && count > 0)
            pageable = PageRequest.of(skip, count, sort);

        return pageable;
    };


    // most complex method
    // TODO: make changes later if necessary
    @Override
    public RideDto orderRide(RideOrderDto rideOrderDto) {
        // if there are no waypoints throw error
        if (rideOrderDto.getWaypoints() == null || rideOrderDto.getWaypoints().size() == 1 || rideOrderDto.getWaypoints().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one waypoint is required to order a ride");
        }
        // if creator id is null or zero throw error
        if (rideOrderDto.getCreatorId() == null || rideOrderDto.getCreatorId() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Creator id is required to order a ride");
        }

        Boolean existsDriverWithActiveStatus = driverRepository.existsDriverWithActiveStatus(); // activer or on ride, same thing
        if (!existsDriverWithActiveStatus) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No available drivers found for the ride");
        }
        Boolean areAllDriversOnRideWithPendingRides = driverRepository.areAllDriversOnRideWithPendingRides();
        if (areAllDriversOnRideWithPendingRides) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "All drivers are currently busy");
        }

        // at this point, we can create the ride from a active driver or one that is not on ride, and has no pending rides
        // first try to find active driver
        Driver assignedDriver = null;
        List<Driver> activeDrivers = driverRepository.findByStatus(UserStatus.ACTIVE);
        List<Driver> eligibleDrivers = activeDrivers.stream()
                .filter(d -> d.getStats() != null && d.getStats().getActivePast24Hours() <= 480 && d.getVehicle() != null // at most 8 hours active in past 24 hours
                        && d.getVehicle().getType() == rideOrderDto.getVehicleType()
                        && (!rideOrderDto.getBabyFriendly() || d.getVehicle().getBabyFriendly())
                        && (!rideOrderDto.getPetFriendly() || d.getVehicle().getPetFriendly())
                ).toList();

        if (!eligibleDrivers.isEmpty()) {
            // assign the first active driver found
            assignedDriver = eligibleDrivers.get(0); // we should actually calculate driver distance from starting point, but right now i don't have that data (where vehicle waypoints are stored)
            // TODO: implement distance calculation later
        } else {
            // from drivers that are on ride, and has no pending rides, find one that is on ride that has closes end time to now
            List<Driver> onRideDrivers = driverRepository.findByStatus(UserStatus.ON_RIDE);
           // eliminate all that has a ride that has scheduled time in future
            List<Ride> pendingRides = rideRepository.findByStatus(RideStatus.ACCEPTED);
            // for every ride, if driver is in onRideDrivers, remove him from onRideDrivers
            for (Ride ride : pendingRides) {
                onRideDrivers.removeIf(d -> d.getId().equals(ride.getDriver().getId()));
            }

            // now filter elgible by vehicle type and baby/pet friendly
            eligibleDrivers = onRideDrivers.stream()
                    .filter(d -> d.getVehicle() != null && d.getVehicle().getType() == rideOrderDto.getVehicleType()
                            && (!rideOrderDto.getBabyFriendly() || d.getVehicle().getBabyFriendly())
                            && (!rideOrderDto.getPetFriendly() || d.getVehicle().getPetFriendly())
                            && d.getStats() != null && d.getStats().getActivePast24Hours() <= 480 // at most 8 hours active in past 24 hours
                    ).toList();

            if (eligibleDrivers.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No suitable drivers found for the ride");
            }
            // TODO: assign by least time to current time from ride end time
            assignedDriver = eligibleDrivers.get(0); // placeholder
        }

        if (assignedDriver == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No suitable drivers found for the ride");
        }

        // create ride entity and save to database
        Ride newRide = new Ride();
        User creator = userRepository.findById(rideOrderDto.getCreatorId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Creator user not found"));
        // start time comes from frontend in string format ready for conversion in LocalDateTime
        String t = rideOrderDto.getScheduledTime();

        newRide.setStartTime(
                (t == null || t.isBlank())
                        ? LocalDateTime.now().withNano(0)
                        : LocalDateTime.parse(t)
        );
        // we have required time in seconds, so append on start time (if end time changes in the future, we will update it later)
        newRide.setEndTime(newRide.getStartTime().plusSeconds(rideOrderDto.getRequiredTime().longValue()));
        newRide.setCreator(creator);
        newRide.setDistance(rideOrderDto.getDistance());
        newRide.setPrice(rideOrderDto.getPrice());
        newRide.setStatus(RideStatus.PENDING); // initially pending (accepted is sufficient in my opinion)
        newRide.setDriver(assignedDriver);

        List<User> passengers = new ArrayList<>();

        for (String email : rideOrderDto.getPassengersEmails()) {
            userRepository.findByEmail(email)
                    .ifPresent(passengers::add);
        }

        newRide.setPassengers(passengers);

        // go through waypoints and create waypoint entities
        List<WaypointDto> waypointDtos = rideOrderDto.getWaypoints();
        List<Waypoint> waypoints = new ArrayList<>();
        for (WaypointDto waypointDto : waypointDtos) {
            Waypoint waypoint = new Waypoint();
            waypoint.setLabel(waypointDto.getLabel());
            waypoint.setLatitude(waypointDto.getLatitude());
            waypoint.setLongitude(waypointDto.getLongitude());
            waypoints.add(waypoint);
        }

        newRide.setWaypoints(waypoints);
        newRide.setFavorite(false); // initially not favorite
        newRide.setPanic(false); // initially no panic
        rideRepository.save(newRide);

        RideDto createdRideDto = new RideDto();
        createdRideDto.setId(newRide.getId());
        createdRideDto.setStartTime(newRide.getStartTime().toString());
        createdRideDto.setEndTime(newRide.getEndTime().toString());
        createdRideDto.setWaypoints(newRide.getWaypoints().stream().map(WaypointDto::new).toList());
        createdRideDto.setDriver(new UserDto(assignedDriver));
        createdRideDto.setPassengers(passengers.stream().map(UserDto::new).toList());
        createdRideDto.setDistance(newRide.getDistance());
        createdRideDto.setPrice(newRide.getPrice());
        createdRideDto.setStatus(newRide.getStatus());

        webSocketNotificationService.sendRideAssigned(assignedDriver.getId(), new RideAssignmentNotification(
                NotificationType.RIDE_ASSIGNED.name(),
                createdRideDto));

        // if ride is scheduled, start a reminder process
        if (newRide.getStartTime().isAfter(LocalDateTime.now())) {
            rideReminderService.start(rideOrderDto.getCreatorId(), newRide.getStartTime());
        }

        // schedule a current ride upadte
        rideReminderService.sendCurrentRideUpdate(rideOrderDto.getCreatorId(), createdRideDto, newRide.getStartTime());
        rideReminderService.sendCurrentRideUpdate(assignedDriver.getId(), createdRideDto, newRide.getStartTime());


        return createdRideDto;
    }

    @Override
    public RideDto cancelRideByDriver(Long rideId, String reason) {

        Ride ride = this.getRideOrThrow(rideId);

        this.checkRidePrivilege(ride.getDriver().getId());

        if (ride.getStatus() != RideStatus.PENDING)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ride cannot be cancelled");

        ride.setStatus(RideStatus.CANCELLED);
        ride.setCanceledBy(ride.getDriver());
        ride = rideRepository.save(ride);
        RideDto rideDto = new RideDto(ride);

        CancelNotification cancelNotification = new CancelNotification(reason, rideDto);
        webSocketNotificationService.sendRideCancelledToUser(ride.getCreator().getId(), cancelNotification);

        return rideDto;
    }

    @Override
    public void activatePanic(Long rideId) {
        Ride ride = getRideOrThrow(rideId);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        if (ride.getDriver().getId().equals(user.getId()) || ride.getCreator().getId().equals(user.getId())) {

            PanicNotification panic = new PanicNotification();
            panic.setRideId(rideId);
            panic.setTimestamp(LocalDateTime.now());
            panic.setTriggeredBy(user.getRole().name());
            panic.setDriverId(ride.getDriver().getId());
            panicRepository.save(panic);

            ride.setPanic(true);
            rideRepository.save(ride);

            webSocketNotificationService.sendPanicNotification(panic);
        }
        else
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your ride");
    }

    @Override
    public List<PanicNotification> getPanics() {
        return panicRepository.findAllByOrderByTimestampDesc();
    }

    @Override
    public RideDto cancelRide(Long rideId) {
        Ride ride = this.getRideOrThrow(rideId);

        this.checkRidePrivilege(ride.getCreator().getId());

        LocalDateTime now = LocalDateTime.now();
        if (ride.getStartTime() != null && ride.getStartTime().minusMinutes(10).isBefore(now)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot cancel ride less than 10 minutes before start");
        }

        ride.setStatus(RideStatus.CANCELLED);
        ride.setCanceledBy(ride.getCreator());
        ride = rideRepository.save(ride);
        RideDto rideDto = new RideDto(ride);

        CancelNotification cancelNotification = new CancelNotification(null, rideDto);
        webSocketNotificationService.sendRideCancelledToUser(ride.getDriver().getId(), cancelNotification);

        return rideDto;
    }

    private void checkRidePrivilege(Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        if (!userId.equals(user.getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your ride");
    }

    private Ride getRideOrThrow(Long rideId) {
        return rideRepository.findById(rideId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found"));
    }
}
