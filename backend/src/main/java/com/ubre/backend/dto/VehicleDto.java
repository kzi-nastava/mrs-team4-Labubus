package com.ubre.backend.dto;

// Driver and this object go together basically

import com.ubre.backend.enums.VehicleType;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    public String getPlates() {
        return plates;
    }

    public void setPlates(String plates) {
        this.plates = plates;
    }

    public Integer getSeats() {
        return seats;
    }

    public void setSeats(Integer seats) {
        this.seats = seats;
    }

    public Boolean isBabyFriendly() {
        return babyFriendly;
    }

    public void setBabyFriendly(Boolean babyFriendly) {
        this.babyFriendly = babyFriendly;
    }

    public Boolean isPetFriendly() {
        return petFriendly;
    }

    public void setPetFriendly(Boolean petFriendly) {
        this.petFriendly = petFriendly;
    }
}
