package com.ubre.backend.controller;

import com.ubre.backend.dto.*;
import com.ubre.backend.service.UserService;
import com.ubre.backend.service.impl.SseService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SseService sseService;

    //getting user details by id
    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        UserDto user = userService.getUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    // getting users avatar by id
    @GetMapping(
            value = "/{id}/avatar",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public ResponseEntity<Resource> getUserAvatar(@PathVariable Long id) {
        Resource avatar = userService.getAvatar(id);
        return ResponseEntity.status(HttpStatus.OK).body(avatar);
    }

    // upload user avatar (note: this endpoint doesn't change avatar url in user profile, that should be done separately)
    @PostMapping(
            value = "/{id}/avatar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Void> uploadUserAvatar(@PathVariable Long id, @RequestParam("file") MultipartFile avatar) {
        userService.uploadAvatar(id, avatar);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
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
            value = "/change-password",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> changePassword(@RequestBody PasswordChangeDto passwordChangeDto) {
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
//    @PostMapping(
//            value="/profile-change",
//            consumes = MediaType.APPLICATION_JSON_VALUE,
//            produces = MediaType.APPLICATION_JSON_VALUE
//    )
//    public ResponseEntity<UserDto> updateUser(@RequestBody ProfileChangeDto profileChangeDto) {
//        UserDto updatedUser = userService.updateUser(profileChangeDto);
//        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
//    }


    // send a passenger request
    @PostMapping(
            value="/{id}/passenger-request", // id is user id that sends the request
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> sendPassengerRequest(@PathVariable Long id, @RequestBody String email) {
        userService.sendPassengerRequest(id, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }


    // register a new passenger account
    @PostMapping(
            value = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserDto> register(@Valid @RequestBody UserRegistrationDto registrationDto) {
        UserDto createdUser = userService.registerUser(registrationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
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

    // this endpoint only updates userdto fields, not password or avatar
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("#id == @securityUtil.currentUserId()")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto updateUserDto) {
        UserDto user = userService.updateUser(updateUserDto);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sse(@RequestParam Long userId, HttpServletResponse response) {
        response.setHeader("Content-Type", "text/event-stream");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");
        response.setHeader("X-Accel-Buffering", "no");
        response.setHeader("Content-Encoding", "identity");
        return sseService.connect(userId);
    }
}
