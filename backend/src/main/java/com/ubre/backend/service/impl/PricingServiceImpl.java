package com.ubre.backend.service.impl;

import com.ubre.backend.dto.VehicleTypePricingDto;
import com.ubre.backend.enums.VehicleType;
import com.ubre.backend.model.VehicleTypePricing;
import com.ubre.backend.repository.VehicleTypePricingRepository;
import com.ubre.backend.service.PricingService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PricingServiceImpl implements PricingService {

    @Autowired
    private VehicleTypePricingRepository pricingRepository;

    @PostConstruct
    public void seedDefaults() {
        for (VehicleType type : VehicleType.values()) {
            if (pricingRepository.findByVehicleType(type).isEmpty()) {
                double basePrice;
                switch (type) {
                    case STANDARD: basePrice = 5.0; break;
                    case LUXURY:   basePrice = 20.0; break;
                    case VAN:      basePrice = 8.0; break;
                    default:       basePrice = 5.0;
                }
                VehicleTypePricing pricing = new VehicleTypePricing(type, basePrice, 1.2);
                pricingRepository.save(pricing);
            }
        }
    }

    @Override
    public VehicleTypePricingDto getPricing() {
        VehicleTypePricing standard = getOrThrow(VehicleType.STANDARD);
        VehicleTypePricing luxury = getOrThrow(VehicleType.LUXURY);
        VehicleTypePricing van = getOrThrow(VehicleType.VAN);

        VehicleTypePricingDto dto = new VehicleTypePricingDto();
        dto.setPricePerKm(standard.getPricePerKm());
        dto.setStandardBasePrice(standard.getBasePrice());
        dto.setLuxuryBasePrice(luxury.getBasePrice());
        dto.setVanBasePrice(van.getBasePrice());
        return dto;
    }

    @Override
    public VehicleTypePricingDto updatePricing(VehicleTypePricingDto dto) {
        VehicleTypePricing standard = getOrThrow(VehicleType.STANDARD);
        VehicleTypePricing luxury = getOrThrow(VehicleType.LUXURY);
        VehicleTypePricing van = getOrThrow(VehicleType.VAN);

        standard.setBasePrice(dto.getStandardBasePrice());
        standard.setPricePerKm(dto.getPricePerKm());
        standard.setEffectiveFrom(LocalDateTime.now());

        luxury.setBasePrice(dto.getLuxuryBasePrice());
        luxury.setPricePerKm(dto.getPricePerKm());
        luxury.setEffectiveFrom(LocalDateTime.now());

        van.setBasePrice(dto.getVanBasePrice());
        van.setPricePerKm(dto.getPricePerKm());
        van.setEffectiveFrom(LocalDateTime.now());

        pricingRepository.save(standard);
        pricingRepository.save(luxury);
        pricingRepository.save(van);

        return getPricing();
    }

    @Override
    public VehicleTypePricing getPricingForType(VehicleType vehicleType) {
        return getOrThrow(vehicleType);
    }

    private VehicleTypePricing getOrThrow(VehicleType type) {
        return pricingRepository.findByVehicleType(type)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Pricing not found for vehicle type: " + type));
    }
}
