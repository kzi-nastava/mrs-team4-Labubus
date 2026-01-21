package com.ubre.backend.model;

import com.ubre.backend.dto.RideDto;
import com.ubre.backend.enums.RideStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "rides")
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "distance")
    private Double distance; // in meters

    @Column(name = "price")
    private Double price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RideStatus status = RideStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @ManyToMany
    @JoinTable(
        name = "ride_passengers",
        joinColumns = @JoinColumn(name = "ride_id"),
        inverseJoinColumns = @JoinColumn(name = "passenger_id")
    )
    private List<User> passengers;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Waypoint> waypoints;

    @OneToOne(mappedBy = "ride", cascade = CascadeType.ALL)
    private Review review;

    @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL)
    private List<Notification> notifications = new ArrayList<>();

    @Column(nullable = false)
    private Boolean panic;

    @Column(nullable = false)
    private Boolean favorite;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "canceled_by")
    private User canceledBy;

    @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL)
    private List<Complaint> complaints = new ArrayList<>();

    public Ride(LocalDateTime startTime, RideStatus status, User creator, Driver driver, List<Waypoint> waypoints) {
        this.startTime = startTime;
        this.status = status;
        this.creator = creator;
        this.driver = driver;
        this.waypoints = waypoints;
        this.panic = false;
        this.favorite = false;
        this.canceledBy = null;
        this.passengers = List.of(creator);
    }

    public Ride(RideDto dto) {
        this.id = dto.getId();
        this.startTime = LocalDateTime.parse(dto.getStartTime());
        this.waypoints = dto.getWaypoints().stream().map(Waypoint::new).toList();
        this.panic = false;
        this.favorite = false;
        this.canceledBy = null;
        this.passengers = new ArrayList<>();
    }
}
