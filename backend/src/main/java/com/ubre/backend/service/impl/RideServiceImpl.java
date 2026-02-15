package com.ubre.backend.service.impl;

import com.ubre.backend.dto.*;
import com.ubre.backend.enums.*;
import com.ubre.backend.model.*;
import com.ubre.backend.repository.*;
import com.ubre.backend.service.EmailService;
import com.ubre.backend.service.RideService;
import com.ubre.backend.service.VehicleService;
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

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private WaypointRepository waypointRepository;

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

        ride.setEndTime(LocalDateTime.now());
        ride.setStatus(RideStatus.COMPLETED);
        ride.getDriver().setStatus(UserStatus.ACTIVE);

        if (ride.getWaypoints().stream().allMatch( w ->w.getVisited() == null || !w.getVisited())) {
            ride.setPrice(0.0);
            ride.setDistance(0.0);
        }
        else if (ride.getWaypoints().stream().anyMatch(w ->w.getVisited() == null || !w.getVisited())) {

            Waypoint endLocation = new Waypoint(
                    waypoint.getLabel(),
                    waypoint.getLatitude(),
                    waypoint.getLongitude(),
                    true
            );

            ride.getWaypoints().removeIf(w ->w.getVisited() == null || !w.getVisited());
            ride.getWaypoints().add(endLocation);
            ride.setDistance(this.estimateDistance(ride));
            ride.setPrice(this.estimateNewPrice(ride));
        }

        rideRepository.save(ride);

        CurrentRideNotification currentRideNotification = new CurrentRideNotification(
                NotificationType.RIDE_COMPLETED.name(),
                null
        );

        this.webSocketNotificationService.sendCurrentRideUpdate(ride.getCreator().getId(), currentRideNotification);
        for (User user : ride.getPassengers())
            emailService.sendRideCompletedEmail(user.getEmail(), ride);
        return ride.getPrice();
    }

    private double estimateNewPrice(Ride ride) {
        double STANDARD_BASE_FARE = 5.0;
        double VAN_BASE_FARE = 8.0;
        double LUXURY_BASE_FARE = 20.0;
        double PER_KM_RATE = 1.2;

        double baseFare;
        VehicleType vehicleType = ride.getDriver().getVehicle().getType();

        if (vehicleType == VehicleType.STANDARD) {
            baseFare = STANDARD_BASE_FARE;
        } else if (vehicleType == VehicleType.VAN) {
            baseFare = VAN_BASE_FARE;
        } else {
            baseFare = LUXURY_BASE_FARE;
        }

        double price = baseFare + (PER_KM_RATE * ride.getDistance());
        double finalPrice = Math.round(price * 100.0) / 100.0;

        return finalPrice;
    }

    private double estimateDistance(Ride ride) {

        StringBuilder URL = new StringBuilder("https://routing.openstreetmap.de/routed-car/route/v1/driving/");
        for (Waypoint w : ride.getWaypoints())
            URL.append(w.getLatitude()).append(",").append(w.getLongitude()).append(";");
        URL.setLength(URL.length()-1);
        URL.append("?overview=full&geometries=geojson&steps=false");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL.toString()))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = null;

        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        String responseString = response.body();

        int summaryIdx =responseString.indexOf("summary")+9;
        int distanceIdx=responseString.indexOf("distance",summaryIdx)+10;
        int dis=responseString.indexOf("}",distanceIdx);

        return Double.parseDouble(responseString.substring(distanceIdx,dis));
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

        // print in console current step - debugging
        System.out.println("Ordering ride for creator id: " + rideOrderDto.getCreatorId());

        Boolean existsDriverWithActiveStatus = driverRepository.existsDriverWithActiveStatus(); // activer or on ride, same thing
        if (!existsDriverWithActiveStatus) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No available drivers found for the ride");
        }

        System.out.println("There are drivers with active status");
        Boolean areAllDriversOnRideWithPendingRides = driverRepository.areAllDriversOnRideWithPendingRides();
        if (areAllDriversOnRideWithPendingRides) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "All drivers are currently busy");
        }

        System.out.println("There are drivers that are not on ride or have no pending rides");

        // at this point, we can create the ride from a active driver or one that is not on ride, and has no pending rides
        // first try to find active driver
        Driver assignedDriver = null;
        List<Driver> activeDrivers = driverRepository.findByStatus(UserStatus.ACTIVE)
                .stream()
                .filter(d -> !Boolean.TRUE.equals(d.getIsBlocked()))
                .toList();

        // print all active drivers in pretty format
        System.out.println("Active drivers:");
        for (Driver d : activeDrivers) {
            System.out.println("Driver ID: " + d.getId() + ", Name: " + d.getName() + ", Active past 24 hours: " + (d.getStats() != null ? d.getStats().getActivePast24Hours() : "N/A") + " minutes, Vehicle type: " + (d.getVehicle() != null ? d.getVehicle().getType() : "N/A") + ", Baby friendly: " + (d.getVehicle() != null ? d.getVehicle().getBabyFriendly() : "N/A") + ", Pet friendly: " + (d.getVehicle() != null ? d.getVehicle().getPetFriendly() : "N/A"));
        }

        List<Driver> eligibleDrivers = activeDrivers.stream()
                .filter(d -> d.getStats() != null && d.getStats().getActivePast24Hours() <= 480 && d.getVehicle() != null // at most 8 hours active in past 24 hours
                        && d.getVehicle().getType() == rideOrderDto.getVehicleType()
                        && (!rideOrderDto.getBabyFriendly() || d.getVehicle().getBabyFriendly())
                        && (!rideOrderDto.getPetFriendly() || d.getVehicle().getPetFriendly())
                ).toList();

        // print eligible drivers in pretty format
        System.out.println("Eligible active drivers (after filtration):");
        for (Driver d : eligibleDrivers) {
            System.out.println("Driver ID: " + d.getId() + ", Name: " + d.getName() + ", Active past 24 hours: " + (d.getStats() != null ? d.getStats().getActivePast24Hours() : "N/A") + " minutes, Vehicle type: " + (d.getVehicle() != null ? d.getVehicle().getType() : "N/A") + ", Baby friendly: " + (d.getVehicle() != null ? d.getVehicle().getBabyFriendly() : "N/A") + ", Pet friendly: " + (d.getVehicle() != null ? d.getVehicle().getPetFriendly() : "N/A"));
        }

        if (!eligibleDrivers.isEmpty()) {
            // assign the first active driver found
            System.out.println("Found eligible active drivers, assigning the first one");
            assignedDriver = eligibleDrivers.get(0); // we should actually calculate driver distance from starting point, but right now i don't have that data (where vehicle waypoints are stored)
            // TODO: implement distance calculation later
        } else {
            System.out.println("No eligible active drivers found, looking for on ride drivers with no pending rides");
            // from drivers that are on ride, and has no pending rides, find one that is on ride that has closes end time to now
            List<Driver> onRideDrivers = driverRepository.findByStatus(UserStatus.ON_RIDE)
                    .stream()
                    .filter(d -> !Boolean.TRUE.equals(d.getIsBlocked()))
                    .collect(Collectors.toList());

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

            System.out.println("Found " + eligibleDrivers.size() + " eligible on-ride drivers with no pending rides");

            if (eligibleDrivers.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No suitable drivers found for the ride");
            }
            // TODO: assign by least time to current time from ride end time
            assignedDriver = eligibleDrivers.get(0); // placeholder
        }

        System.out.println("Driver assigned, checking if its null");

        if (assignedDriver == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No suitable drivers found for the ride");
        }

        System.out.println("Driver assigned successfully, creating ride entity");

        // create ride entity and save to database
        Ride newRide = new Ride();
        User creator = userRepository.findById(rideOrderDto.getCreatorId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Creator user not found"));
        // quick check if creator is blocked, if yes, throw error
        if (Boolean.TRUE.equals(creator.getIsBlocked())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is blocked from creating rides");
        }
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

        passengers.add(creator);
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
        String username = auth.getName();
        User user = userRepository.findByEmail(username).orElseThrow();

        if (!userId.equals(user.getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your ride");
    }

    private Ride getRideOrThrow(Long rideId) {
        return rideRepository.findById(rideId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found"));
    }
}
