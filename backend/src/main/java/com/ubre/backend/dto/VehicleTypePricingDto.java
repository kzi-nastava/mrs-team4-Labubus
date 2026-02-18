package com.ubre.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleTypePricingDto {
    private double pricePerKm;
    private double standardBasePrice;
    private double luxuryBasePrice;
    private double vanBasePrice;
}
