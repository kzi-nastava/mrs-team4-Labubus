package com.ubre.backend.model;

import com.ubre.backend.enums.VehicleType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "vehicle_type_pricing")
public class VehicleTypePricing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false, unique = true)
    private VehicleType vehicleType;

    @Column(name = "base_price", nullable = false)
    private double basePrice;

    @Column(name = "price_per_km", nullable = false)
    private double pricePerKm;

    @Column(name = "effective_from", nullable = false)
    private LocalDateTime effectiveFrom;

    // Constructors
    public VehicleTypePricing() {
        this.effectiveFrom = LocalDateTime.now();
    }

    public VehicleTypePricing(VehicleType vehicleType, double basePrice, double pricePerKm) {
        this.vehicleType = vehicleType;
        this.basePrice = basePrice;
        this.pricePerKm = pricePerKm;
        this.effectiveFrom = LocalDateTime.now();
    }
}
