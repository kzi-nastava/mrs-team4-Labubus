package com.ubre.backend.controller;

import com.ubre.backend.dto.UserDto;
import com.ubre.backend.enums.Role;
import com.ubre.backend.enums.UserStatus;
import com.ubre.backend.model.Admin;
import com.ubre.backend.service.EmailService;
import com.ubre.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/admins")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private UserService userService;

    // get all administrators
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserDto>> getAllAdmins() {
        List<UserDto> administrators = userService.getAllUsers().stream()
                .filter(user -> user.getRole() == Role.ADMIN)
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(administrators);
    }

    // create a new administrator profile
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> createAdmin(@RequestBody UserDto adminDto) {
        UserDto createdAdministrator = userService.createAdmin(adminDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAdministrator);
    }

    // get single administrator
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> getAdministrator(@PathVariable Long id) {
        UserDto admin = userService.getUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(admin);
    }
}
