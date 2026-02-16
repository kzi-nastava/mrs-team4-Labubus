package com.ubre.backend.service;

import com.ubre.backend.dto.VehicleTypePricingDto;
import com.ubre.backend.enums.VehicleType;
import com.ubre.backend.model.VehicleTypePricing;

public interface PricingService {
    VehicleTypePricingDto getPricing();
    VehicleTypePricingDto updatePricing(VehicleTypePricingDto dto);
    VehicleTypePricing getPricingForType(VehicleType vehicleType);
}
