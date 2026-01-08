package com.ubre.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@Setter
public class RideDto implements Serializable {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private WaypointDto[] waypoints;
    private UserDto driver;
    private Collection<UserDto> passengers;
    private Boolean panic;
    private String canceledBy;
    private Double price;
    private Double distance;

    public RideDto() {
    }

    public RideDto(Long id, LocalDateTime start, LocalDateTime end, WaypointDto[] waypoints, UserDto driver, Collection<UserDto> passengers, boolean panic, String canceledBy, double price, double distance) {
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
    }
}