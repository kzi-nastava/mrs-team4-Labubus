package com.ubre.backend.controller;

import com.ubre.backend.dto.PasswordChangeDto;
import com.ubre.backend.dto.ProfileChangeDto;
import com.ubre.backend.dto.UserDto;
import com.ubre.backend.dto.UserStatsDto;
import com.ubre.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    //getting user details by id
    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        UserDto user = userService.getUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    // getting user stats by id
    @GetMapping(
            value = "/{id}/stats",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserStatsDto> getUserStats(@PathVariable Long id) {
        UserStatsDto userStats = userService.getUserStats(id);
        return ResponseEntity.status(HttpStatus.OK).body(userStats);
    }

    // password change endpoint
    @PutMapping(
            value = "/{id}/change-password",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> changePassword(@PathVariable Long id, @RequestBody PasswordChangeDto passwordChangeDto) {
        userService.changePassword(passwordChangeDto);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    // activate user account
    @PutMapping(
            value = "/{id}/activate"
    )
    public ResponseEntity<Void> activateUser(@PathVariable Long id) {
        userService.activateUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    // user profile changes request endpoint
    @PostMapping(
            value="/profile-change",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserDto> updateUser(@RequestBody ProfileChangeDto profileChangeDto) {
        UserDto updatedUser = userService.updateUser(profileChangeDto);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }

    // driver sends request and admin approves or rejects it
    @PostMapping(
            value="/profile-change/request",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> requestProfileChange(@RequestBody ProfileChangeDto profileChangeDto) {
        userService.requestProfileChange(profileChangeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    // get all profile change requests - for admin
    @GetMapping(
            value="/profile-change/requests",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<ProfileChangeDto>> getAllProfileChangeRequests() {
        List<ProfileChangeDto> requests = userService.getAllProfileChangeRequests();
        return ResponseEntity.status(HttpStatus.OK).body(requests);
    }

    // send a passenger request
    @PostMapping(
            value="/{id}/passenger-request", // id is user id that sends the request
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> sendPassengerRequest(@PathVariable Long id, @RequestBody String email) {
        userService.sendPassengerRequest(id, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    // accept a passenger request via email link
    // not sure how to implement this endpoint yet? (questtion for later)



//    @Autowired
//    private UserService userService;
//
//    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<List<UserDTO>> getAllUsers() {
//        List<UserDTO> users = userService.getAllUsers();
//        return new ResponseEntity<>(users, HttpStatus.OK);
//    }
//
//    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
//        try {
//            UserDTO user = userService.getUserById(id);
//            return new ResponseEntity<>(user, HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }
//
//    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<UserDTO> updateUser(
//            @PathVariable Long id,
//            @RequestBody UpdateUserDTO updateUserDTO) {
//        try {
//            UserDTO user = userService.updateUser(id, updateUserDTO);
//            return new ResponseEntity<>(user, HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//    }
//
//    @DeleteMapping(value = "/{id}")
//    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
//        try {
//            userService.deleteUser(id);
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }
//
//    @PutMapping(value = "/{id}/block")
//    public ResponseEntity<Void> blockUser(@PathVariable Long id) {
//        try {
//            userService.blockUser(id);
//            return new ResponseEntity<>(HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }
//
//    @PutMapping(value = "/{id}/unblock")
//    public ResponseEntity<Void> unblockUser(@PathVariable Long id) {
//        try {
//            userService.unblockUser(id);
//            return new ResponseEntity<>(HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }
}
