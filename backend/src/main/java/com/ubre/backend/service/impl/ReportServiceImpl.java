package com.ubre.backend.service.impl;

import com.ubre.backend.dto.ReportDailyDataDto;
import com.ubre.backend.dto.ReportRequestDto;
import com.ubre.backend.dto.ReportResponseDto;
import com.ubre.backend.dto.ReportSummaryDto;
import com.ubre.backend.enums.RideStatus;
import com.ubre.backend.enums.Role;
import com.ubre.backend.model.Ride;
import com.ubre.backend.model.User;
import com.ubre.backend.repository.RideRepository;
import com.ubre.backend.repository.UserRepository;
import com.ubre.backend.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);

    @Autowired
    private RideRepository rideRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public ReportResponseDto generateReport(ReportRequestDto request) {
        LocalDate dateFrom = parseDateOrThrow(request.getDateFrom(), "dateFrom");
        LocalDate dateTo = parseDateOrThrow(request.getDateTo(), "dateTo");

        if (dateFrom.isAfter(dateTo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "dateFrom must be before or equal to dateTo");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        User currentUser = (User) auth.getPrincipal();
        Role currentRole = currentUser.getRole();

        String scope = normalizeScope(request.getScope(), currentRole);
        User targetUser = null;

        if (currentRole == Role.ADMIN && "single_user".equals(scope)) {
            if (request.getUserEmail() == null || request.getUserEmail().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userEmail is required for single_user scope");
            }
            targetUser = userRepository.findByEmail(request.getUserEmail())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        }

        LocalDateTime start = dateFrom.atStartOfDay();
        LocalDateTime endExclusive = dateTo.plusDays(1).atStartOfDay();

        List<Ride> rides = rideRepository.findByStatusInAndStartTimeBetween(
                List.of(RideStatus.COMPLETED), start, endExclusive
        );
        logger.info("REPORT: scope={}, userId={}, dateFrom={}, dateTo={}, start={}, endExclusive={}, ridesFound={}",
                scope, currentUser.getId(), dateFrom, dateTo, start, endExclusive, rides.size());
        rides.stream()
                .filter(r -> r.getStartTime() != null)
                .limit(5)
                .forEach(r -> logger.info("REPORT: sample ride id={}, startTime={}, driverId={}",
                        r.getId(), r.getStartTime(), r.getDriver() != null ? r.getDriver().getId() : null));

        Map<LocalDate, DailyAccumulator> daily = initializeDaily(dateFrom, dateTo);

        if ("self".equals(scope)) {
            accumulateForUser(daily, rides, currentUser);
        } else if ("all_drivers".equals(scope)) {
            accumulateForAllDrivers(daily, rides);
        } else if ("all_passengers".equals(scope)) {
            accumulateForAllPassengers(daily, rides);
        } else if ("single_user".equals(scope)) {
            accumulateForUser(daily, rides, targetUser);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid scope");
        }

        List<ReportDailyDataDto> dailyData = buildDailyData(dateFrom, dateTo, daily);
        ReportSummaryDto summary = buildSummary(dailyData);

        return new ReportResponseDto(dailyData, summary);
    }

    private LocalDate parseDateOrThrow(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + " is required");
        }
        try {
            return LocalDate.parse(value, DATE_FORMAT);
        } catch (DateTimeParseException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid " + field + " format");
        }
    }

    private String normalizeScope(String scope, Role role) {
        if (role != Role.ADMIN) {
            return "self";
        }
        if (scope == null || scope.isBlank()) {
            return "self";
        }
        if (!scope.equals("self") && !scope.equals("all_drivers") && !scope.equals("all_passengers") && !scope.equals("single_user")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid scope");
        }
        return scope;
    }

    private Map<LocalDate, DailyAccumulator> initializeDaily(LocalDate from, LocalDate to) {
        Map<LocalDate, DailyAccumulator> map = new HashMap<>();
        LocalDate cur = from;
        while (!cur.isAfter(to)) {
            map.put(cur, new DailyAccumulator());
            cur = cur.plusDays(1);
        }
        return map;
    }

    private void accumulateForUser(Map<LocalDate, DailyAccumulator> daily, List<Ride> rides, User user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        Long userId = user.getId();
        for (Ride ride : rides) {
            if (ride.getStartTime() == null) {
                continue;
            }
            LocalDate day = ride.getStartTime().toLocalDate();
            DailyAccumulator acc = daily.get(day);
            if (acc == null) {
                continue;
            }

            boolean isDriver = ride.getDriver() != null && ride.getDriver().getId().equals(userId);
            boolean isPassenger = (ride.getCreator() != null && ride.getCreator().getId().equals(userId)) ||
                    (ride.getPassengers() != null &&
                            ride.getPassengers().stream().anyMatch(p -> p.getId().equals(userId)));

            if (!isDriver && !isPassenger) {
                continue;
            }

            acc.rideCount += 1;
            acc.distanceKm += metersToKm(ride.getDistance());

            if (isDriver) {
                acc.amountMoney += safeAmount(ride.getPrice());
            } else {
                acc.amountMoney -= safeAmount(ride.getPrice());
            }
        }
    }

    private void accumulateForAllDrivers(Map<LocalDate, DailyAccumulator> daily, List<Ride> rides) {
        for (Ride ride : rides) {
            if (ride.getDriver() == null) {
                continue;
            }
            if (ride.getStartTime() == null) {
                continue;
            }
            LocalDate day = ride.getStartTime().toLocalDate();
            DailyAccumulator acc = daily.get(day);
            if (acc == null) {
                continue;
            }
            acc.rideCount += 1;
            acc.distanceKm += metersToKm(ride.getDistance());
            acc.amountMoney += safeAmount(ride.getPrice());
        }
    }

    private void accumulateForAllPassengers(Map<LocalDate, DailyAccumulator> daily, List<Ride> rides) {
        for (Ride ride : rides) {
            if (ride.getStartTime() == null) {
                continue;
            }
            LocalDate day = ride.getStartTime().toLocalDate();
            DailyAccumulator acc = daily.get(day);
            if (acc == null) {
                continue;
            }
            List<User> passengers = new ArrayList<>();
            if (ride.getPassengers() != null) {
                passengers.addAll(ride.getPassengers());
            }
            if (ride.getCreator() != null && passengers.stream().noneMatch(p -> p.getId().equals(ride.getCreator().getId()))) {
                passengers.add(ride.getCreator());
            }
            for (User passenger : passengers) {
                acc.rideCount += 1;
                acc.distanceKm += metersToKm(ride.getDistance());
                acc.amountMoney -= safeAmount(ride.getPrice());
            }
        }
    }

    private List<ReportDailyDataDto> buildDailyData(LocalDate from, LocalDate to, Map<LocalDate, DailyAccumulator> daily) {
        List<ReportDailyDataDto> result = new ArrayList<>();
        LocalDate cur = from;
        while (!cur.isAfter(to)) {
            DailyAccumulator acc = daily.get(cur);
            if (acc == null) {
                acc = new DailyAccumulator();
            }
            result.add(new ReportDailyDataDto(
                    cur.format(DATE_FORMAT),
                    acc.rideCount,
                    round2(acc.distanceKm),
                    round2(acc.amountMoney)
            ));
            cur = cur.plusDays(1);
        }
        return result;
    }

    private ReportSummaryDto buildSummary(List<ReportDailyDataDto> dailyData) {
        int totalRides = 0;
        double totalDistance = 0.0;
        double totalAmount = 0.0;

        for (ReportDailyDataDto d : dailyData) {
            totalRides += d.getRideCount();
            totalDistance += d.getDistanceKm();
            totalAmount += d.getAmountMoney();
        }

        int days = dailyData.size();
        double avgRides = days == 0 ? 0.0 : (double) totalRides / days;
        double avgDistance = days == 0 ? 0.0 : totalDistance / days;
        double avgMoney = days == 0 ? 0.0 : totalAmount / days;

        return new ReportSummaryDto(
                totalRides,
                round2(totalDistance),
                round2(totalAmount),
                round2(avgRides),
                round2(avgDistance),
                round2(avgMoney)
        );
    }

    private double metersToKm(Double meters) {
        if (meters == null) {
            return 0.0;
        }
        return meters / 1000.0;
    }

    private double safeAmount(Double amount) {
        return amount == null ? 0.0 : amount;
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private static class DailyAccumulator {
        int rideCount = 0;
        double distanceKm = 0.0;
        double amountMoney = 0.0;
    }
}
