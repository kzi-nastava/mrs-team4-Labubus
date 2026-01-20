package com.ubre.backend.dto;

import com.ubre.backend.model.Ride;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RideDto implements Serializable {
    private Long id;
    private String start; // ISO 8601 format
    private String end;
    private List<WaypointDto> waypoints;
    private UserDto driver;
    private List<UserDto> passengers;
    private Boolean panic;
    private Long canceledBy;
    private Double price;
    private Double distance;

    public RideDto(Long id, String start, String end, List<WaypointDto> waypoints, UserDto driver, List<UserDto> passengers, Boolean panic, Long canceledBy, Double price, Double distance) {
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

    public RideDto(Ride model) {
        this.id = model.getId();
        this.start = model.getStartTime().toString();
        this.end = model.getEndTime().toString();
        this.waypoints = model.getWaypoints().stream().map(WaypointDto::new).toList();
        this.driver = new UserDto(model.getDriver());
        this.passengers = model.getPassengers().stream().map(UserDto::new).toList();
        this.panic = model.getPanic();
        this.canceledBy = model.getCanceledBy().getId();
        this.price = model.getPrice();
        this.distance = model.getDistance();
    }
}