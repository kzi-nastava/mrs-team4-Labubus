package com.ubre.backend.dto;

// Driver and this object go together basically

import com.ubre.backend.enums.VehicleType;
import com.ubre.backend.model.Vehicle;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VehicleDto implements java.io.Serializable {
    private Long id;
    private String model;
    private VehicleType type;
    private String plates;
    private Integer seats;
    private Boolean babyFriendly;
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
