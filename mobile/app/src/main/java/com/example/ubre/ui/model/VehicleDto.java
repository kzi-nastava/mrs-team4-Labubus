package com.example.ubre.ui.model;

public class VehicleDto {
    private String id;
    private String model;
    private String type;
    private String plates;
    private int seats;
    private boolean babyFriendly;
    private boolean petFriendly;

    public VehicleDto(String id, String model, String type, String plates, int seats, boolean babyFriendly, boolean petFriendly) {
        this.id = id;
        this.model = model;
        this.type = type;
        this.plates = plates;
        this.seats = seats;
        this.babyFriendly = babyFriendly;
        this.petFriendly = petFriendly;
    }

}
