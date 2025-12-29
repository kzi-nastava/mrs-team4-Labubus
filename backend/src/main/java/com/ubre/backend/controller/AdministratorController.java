package com.ubre.backend.controller;

import com.ubre.backend.dto.UserDto;
import com.ubre.backend.enums.Role;
import com.ubre.backend.enums.UserStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdministratorController {

    // get all administrators
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserDto>> getAllAdministrators() {
        List<UserDto> administrators = List.of(
                new UserDto(100L, Role.ADMIN, "", "admin1@ubre.com", "Admin", "One", "111-111", "Admin Street 1", UserStatus.ACTIVE),
                new UserDto(101L, Role.ADMIN, "", "admin2@ubre.com", "Admin", "Two", "222-222", "Admin Street 2", UserStatus.ACTIVE)
        );
        return ResponseEntity.status(HttpStatus.OK).body(administrators);
    }

    // create a new administrator profile
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> createAdministrator(@RequestBody UserDto administratorDto) {
        UserDto createdAdministrator = new UserDto(
                102L,
                Role.ADMIN,
                administratorDto.getAvatarUrl(),
                administratorDto.getEmail(),
                administratorDto.getName(),
                administratorDto.getSurname(),
                administratorDto.getPhone(),
                administratorDto.getAddress(),
                UserStatus.ACTIVE
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAdministrator);
    }

    // get single administrator
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> getAdministrator(@PathVariable Long id) {
        UserDto administrator = new UserDto(id, Role.ADMIN, "", "admin@ubre.com", "Admin", "Profile", "000-111", "Admin Street", UserStatus.ACTIVE);
        return ResponseEntity.status(HttpStatus.OK).body(administrator);
    }
}
