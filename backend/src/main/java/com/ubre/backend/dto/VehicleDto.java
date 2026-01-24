package com.ubre.backend.dto;

// Driver and this object go together basically

import com.ubre.backend.enums.VehicleType;
import com.ubre.backend.model.Vehicle;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.AnyDiscriminator;

@Getter
@Setter
@NoArgsConstructor
public class VehicleDto implements java.io.Serializable {
    private Long id;
    @NotBlank(message = "Model is required")
    private String model;
    @NotNull(message = "Type is required")
    private VehicleType type;
    @NotBlank(message = "Plates are required")
    private String plates;
    @NotNull(message = "Seats are required")
    @Min(value = 2, message = "There must be at least 2 seats")
    private Integer seats;
    @NotNull(message = "Baby friendly field is required")
    private Boolean babyFriendly;
    @NotNull(message = "Pet friendly field is required")
    private Boolean petFriendly;

    public VehicleDto(Long id, String model, VehicleType type, String plates, Integer seats, Boolean babyFriendly, Boolean petFriendly) {
        this.id = id;
        this.model = model;
        this.type = type;
        this.plates = plates;
        this.seats = seats;
        this.babyFriendly = babyFriendly;
        this.petFriendly = petFriendly;
    }

    public VehicleDto(Vehicle model) {
        this.id = model.getId();
        this.model = model.getModel();
        this.type = model.getType();
        this.seats = model.getSeats();
        this.babyFriendly = model.getBabyFriendly();
        this.petFriendly = model.getPetFriendly();
        this.plates = model.getPlates();
    }
}
