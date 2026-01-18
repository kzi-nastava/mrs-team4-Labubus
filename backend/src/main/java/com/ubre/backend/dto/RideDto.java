package com.ubre.backend.dto;

import com.ubre.backend.model.Ride;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
public class RideDto implements Serializable {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private WaypointDto[] waypoints;
    private UserDto driver;
    private Collection<UserDto> passengers;
    private Boolean panic;
    private Long canceledBy;
    private Double price;
    private Double distance;
    private Long createdBy;

    public RideDto(Long id, LocalDateTime start, LocalDateTime end, WaypointDto[] waypoints, UserDto driver, Collection<UserDto> passengers, Boolean panic, Long canceledBy, Double price, Double distance, Long createdBy) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.waypoints = waypoints;
        this.driver = driver;
        this.passengers = passengers;
        this.panic = panic;
        this.canceledBy = canceledBy;
        this.price = price;
        this.distance = distance;
        this.createdBy = createdBy;
    }

    public RideDto(Ride model) {
        this.id = model.getId();
        this.start = model.getStartTime();
        this.end = model.getEndTime();
        this.waypoints = model.getWaypoints().stream().map(WaypointDto::new).toArray(WaypointDto[]::new);
        this.driver = new UserDto(model.getDriver());
        this.passengers = model.getPassengers().stream().map(UserDto::new).toList();
        this.panic = model.getPanic();
        this.canceledBy = model.getCanceledBy() != null ? model.getCanceledBy().getId() : null;
        this.price = model.getPrice();
        this.distance = model.getDistance();
        this.createdBy = model.getCreator() == null ? null : model.getCreator().getId();
    }
}