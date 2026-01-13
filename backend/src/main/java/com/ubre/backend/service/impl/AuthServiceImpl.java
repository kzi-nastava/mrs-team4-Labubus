package com.ubre.backend.service.impl;

import com.ubre.backend.enums.UserStatus;
import com.ubre.backend.model.Driver;
import com.ubre.backend.model.User;
import com.ubre.backend.repository.DriverRepository;
import com.ubre.backend.repository.UserRepository;
import com.ubre.backend.service.AuthService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DriverRepository driverRepository;

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updateUserStatus(User user, UserStatus newStatus) {
        user.setStatus(newStatus);
        return userRepository.save(user);
    }

    @Override
    public void logout(String email) throws BadRequestException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (UserStatus.ON_RIDE.equals(user.getStatus())) {
            throw new BadRequestException("Cannot logout while on a ride!");
        }

        updateUserStatus(user, UserStatus.INACTIVE);
    }

    @Override
    public String toggleAvailability(String email) {
        Driver driver = driverRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (driver.getStatus() == UserStatus.ON_RIDE) {
            driver.setPendingInactiveStatus(true);
            driverRepository.save(driver);
            return "You are on a ride. You will be set to INACTIVE automatically after.";
        }

        driver.setStatus(driver.getStatus() == UserStatus.ACTIVE ? UserStatus.INACTIVE : UserStatus.ACTIVE);
        save(driver);
        return "Status changed.";
    }

    @Override
    public void sendResetToken(String email) {

    }


}
