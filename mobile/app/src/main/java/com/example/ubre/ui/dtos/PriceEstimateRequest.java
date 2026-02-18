package com.example.ubre.ui.dtos;

public class PriceEstimateRequest {
    public double distance;
    public int vehicleType;

    public PriceEstimateRequest(double distance, int vehicleType) {
        this.distance = distance;
        this.vehicleType = vehicleType;
    }
}
