package com.ubre.backend.dto;

// Driver and this object go together basically

import com.ubre.backend.enums.VehicleType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
}
