package com.ubre.backend.service.impl;

import com.ubre.backend.dto.UserDto;
import com.ubre.backend.enums.Role;
import com.ubre.backend.enums.UserStatus;
import com.ubre.backend.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    // Mock data for demonstration purposes
    private static List<UserDto> users = new ArrayList<UserDto>();

    public UserServiceImpl() {
        users.add(new UserDto(1L, Role.DRIVER, "avatar1.png", "john@doe.com", "John", "Doe", "1234567890", "123 Main St", UserStatus.ACTIVE));
        users.add(new UserDto(2L, Role.REGISTERED_USER, "avatar2.png", "jane@doe.com", "Jane", "Doe", "0987654321", "456 Elm St", UserStatus.INACTIVE));
        users.add(new UserDto(3L, Role.DRIVER, "avatar3.png", "king@cobra.com", "King", "Cobra", "1122334455", "789 Oak St", UserStatus.ON_RIDE));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return users;
    }

}
