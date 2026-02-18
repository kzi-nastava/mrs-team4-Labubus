package com.example.ubre.ui.dtos;

import java.util.List;

public class RideOrderRequest {
    public long id;
    public long creatorId;
    public List<String> passengersEmails;
    public List<RideOrderWaypoint> waypoints;
    public int vehicleType;
    public boolean babyFriendly;
    public boolean petFriendly;
    public String scheduledTime;
    public double distance;
    public double requiredTime;
    public double price;

    public RideOrderRequest(long id,
                            long creatorId,
                            List<String> passengersEmails,
                            List<RideOrderWaypoint> waypoints,
                            int vehicleType,
                            boolean babyFriendly,
                            boolean petFriendly,
                            String scheduledTime,
                            double distance,
                            double requiredTime,
                            double price) {
        this.id = id;
        this.creatorId = creatorId;
        this.passengersEmails = passengersEmails;
        this.waypoints = waypoints;
        this.vehicleType = vehicleType;
        this.babyFriendly = babyFriendly;
        this.petFriendly = petFriendly;
        this.scheduledTime = scheduledTime;
        this.distance = distance;
        this.requiredTime = requiredTime;
        this.price = price;
    }
}
