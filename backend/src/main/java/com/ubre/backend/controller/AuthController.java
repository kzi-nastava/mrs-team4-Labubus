package com.ubre.backend.controller;

import com.ubre.backend.dto.LoginDto;
import com.ubre.backend.dto.UserDto;
import com.ubre.backend.dto.UserRegistrationDto;
import com.ubre.backend.enums.Role;
import com.ubre.backend.enums.UserStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    // login existing user and return sanitized profile information
    @PostMapping(
            value = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserDto> login(@RequestBody LoginDto loginDto) {
        UserDto authenticatedUser = new UserDto(
                1L,
                Role.REGISTERED_USER,
                "",
                loginDto.getEmail(),
                "Authenticated",
                "User",
                "000-000",
                "Example address",
                UserStatus.ACTIVE
        );
        return ResponseEntity.status(HttpStatus.OK).body(authenticatedUser);
    }

    // register a new passenger account
    @PostMapping(
            value = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserDto> register(@RequestBody UserRegistrationDto registrationDto) {
        UserDto createdUser = new UserDto(
                2L,
                Role.REGISTERED_USER,
                registrationDto.getAvatarUrl(),
                registrationDto.getEmail(),
                registrationDto.getName(),
                registrationDto.getSurname(),
                registrationDto.getPhone(),
                registrationDto.getAddress(),
                UserStatus.INACTIVE
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
}
