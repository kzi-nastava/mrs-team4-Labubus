package com.ubre.backend.repository;

import com.ubre.backend.model.Vehicle;
import com.ubre.backend.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByDriver(Driver driver);
    Optional<Vehicle> findByLicensePlate(String licensePlate);
}
