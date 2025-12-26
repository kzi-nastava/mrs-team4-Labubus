package com.example.ubre.ui.dtos;

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

    public String getId() {
        return id;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }
    public String getSurname() {
        return surname;
    }
    public String getPhone() {
        return phone;
    }
    public String getAddress() {
        return address;
    }
    public VehicleDto getVehicle() {
        return vehicle;
    }
}
