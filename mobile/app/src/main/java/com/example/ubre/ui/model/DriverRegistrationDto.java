package com.example.ubre.ui.model;

public class DriverRegistrationDto {
    private String id;
    private String avatarUrl;
    private String email;
    private String password; // only temporary (this is going to be hashed and stored securely)
    private String name;
    private String surname;
    private String phone;
    private String address;
    private VehicleDto vehicle;

    public DriverRegistrationDto(String id, String avatarUrl, String email, String password, String name, String surname, String phone, String address, VehicleDto vehicle) {
        this.id = id;
        this.avatarUrl = avatarUrl;
        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.address = address;
        this.vehicle = vehicle;
    }
}
