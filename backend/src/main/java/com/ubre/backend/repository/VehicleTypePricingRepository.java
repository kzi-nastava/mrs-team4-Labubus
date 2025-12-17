package com.ubre.backend.repository;

import com.ubre.backend.model.VehicleTypePricing;
import com.ubre.backend.enums.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VehicleTypePricingRepository extends JpaRepository<VehicleTypePricing, Long> {
    Optional<VehicleTypePricing> findByVehicleType(VehicleType vehicleType);
}
