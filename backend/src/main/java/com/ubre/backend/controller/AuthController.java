package com.ubre.backend.controller;

import com.ubre.backend.dto.LoginDto;
import com.ubre.backend.dto.UserDto;
import com.ubre.backend.dto.UserRegistrationDto;
import com.ubre.backend.dto.UserTokenState;
import com.ubre.backend.enums.Role;
import com.ubre.backend.enums.UserStatus;
import com.ubre.backend.model.Driver;
import com.ubre.backend.model.User;
import com.ubre.backend.service.AuthService;
import com.ubre.backend.service.DriverService;
import com.ubre.backend.service.UserService;
import com.ubre.backend.service.impl.CustomUserDetailsService;
import com.ubre.backend.util.TokenUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private TokenUtils tokenUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private AuthService authService;
    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    // login existing user and return sanitized profile information
    @PostMapping("/login")
    public ResponseEntity<UserTokenState> createAuthenticationToken(
            @RequestBody @Valid LoginDto authenticationRequest, HttpServletResponse response) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authenticationRequest.getEmail().trim().toLowerCase(), authenticationRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User) authentication.getPrincipal();
        String jwt = tokenUtils.generateToken(user);
        Long expiresIn = tokenUtils.getExpiredIn();

        authService.updateUserStatus(user, UserStatus.ACTIVE);

        return ResponseEntity.ok(new UserTokenState(jwt, expiresIn));
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(Principal principal) throws BadRequestException {
        authService.logout(principal.getName());
        return ResponseEntity.ok("Successfully logged out.");
    }

    @PutMapping("/status")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<String> changeStatus(Principal principal) {
        String message = authService.toggleAvailability(principal.getName());
        return ResponseEntity.ok(message);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activate(@RequestParam String token) throws BadRequestException {
        authService.activateAccount(token);
        return ResponseEntity.ok("Account successfully activated");
    }

}
