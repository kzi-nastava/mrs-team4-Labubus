package com.ubre.backend.service;

import com.ubre.backend.dto.VehicleDto;
import com.ubre.backend.dto.VehicleIndicatorDto;
import com.ubre.backend.dto.WaypointDto;
import com.ubre.backend.enums.DriverStatus;
import com.ubre.backend.enums.VehicleType;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleServiceImpl implements VehicleService {

    private final Collection<VehicleDto> vehicles = List.of(
            new VehicleDto(1L, "Toyota Carolla 2021", VehicleType.STANDARD, "1231231231", 5, false, false),
            new VehicleDto(2L, "Ford F-150", VehicleType.LUXURY, "545745645", 5, true, false),
            new VehicleDto(3L, "Honda Civic", VehicleType.STANDARD, "34534634634", 4, true, true),
            new VehicleDto(4L, "Jeep Wrangler", VehicleType.VAN, "567858567868", 3, false, true)
    );

    private final Collection<VehicleIndicatorDto> locations = List.of(
            new VehicleIndicatorDto(1L, new WaypointDto(1L, "Pera", 45.17, 19.49), DriverStatus.ACTIVE, false),
            new VehicleIndicatorDto(2L, new WaypointDto(2L, "Mika", 45.51, 19.12), DriverStatus.ACTIVE, true),
            new VehicleIndicatorDto(3L, new WaypointDto(3L, "Djura", 45.87, 19.74), DriverStatus.ON_RIDE, false),
            new VehicleIndicatorDto(4L, new WaypointDto(4L, "Marko", 45.96, 19.73), DriverStatus.ON_RIDE, false)
    );

    @Override
    public VehicleDto createVehicle(Long driverId, VehicleDto createVehicleDto) {
        createVehicleDto.setId(vehicles.stream().mapToLong(VehicleDto::getId).max().getAsLong() + 1L);
        vehicles.add(createVehicleDto);
        return createVehicleDto;
    }

    @Override
    public VehicleDto getVehicleById(Long id) {
        Optional<VehicleDto> targetVehicle = vehicles.stream().filter(vehicle -> vehicle.getId() == id).findFirst();
        return targetVehicle.orElse(null);
    }

    @Override
    public VehicleDto getVehicleByDriver(Long driverId) {
        return null;
    }

    @Override
    public VehicleDto updateVehicle(Long id, VehicleDto updateVehicleDto) {
        return updateVehicleDto;
    }

    @Override
    public VehicleDto deleteVehicle(Long id) {
        VehicleDto vehicle = getVehicleById(id);
        vehicles.remove(vehicle);
        return vehicle;
    }

    @Override
    public Collection<VehicleIndicatorDto> getVehicleIndicators() {
        return locations;
    }

    @Override
    public VehicleIndicatorDto getVehicleIndicator(Long id) {
        Optional<VehicleIndicatorDto> vehicleLocation = locations.stream().filter(location -> location.getDriverId() == id).findFirst();
        return vehicleLocation.orElse(null);
    }
}
