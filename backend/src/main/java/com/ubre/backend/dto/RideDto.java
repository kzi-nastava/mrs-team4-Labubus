package com.ubre.backend.dto;

import com.ubre.backend.model.Ride;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RideDto implements Serializable {
    private Long id;
    private String startTime; // ISO 8601 format
    private String endTime;
    private List<WaypointDto> waypoints;
    private UserDto driver;
    private List<UserDto> passengers;
    private Boolean panic;
    private Long canceledBy;
    private Double price;
    private Double distance;
    private Long createdBy;

    public RideDto(Long id, String startTime, String endTime, List<WaypointDto> waypoints, UserDto driver, List<UserDto> passengers, Boolean panic, Long canceledBy, Double price, Double distance, Long createdBy) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
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
        this.startTime = model.getStartTime().toString();
        this.endTime = model.getEndTime().toString();
        this.waypoints = model.getWaypoints().stream().map(WaypointDto::new).toList();
        this.driver = new UserDto(model.getDriver());
        this.passengers = model.getPassengers().stream().map(UserDto::new).toList();
        this.panic = model.getPanic();
        this.canceledBy = model.getCanceledBy() != null ? model.getCanceledBy().getId() : null;
        this.price = model.getPrice();
        this.distance = model.getDistance();
        this.createdBy = model.getCreator() == null ? null : model.getCreator().getId();
    }
}