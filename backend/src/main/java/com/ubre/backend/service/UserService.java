package com.ubre.backend.service;

import com.ubre.backend.model.User;
import com.ubre.backend.dto.user.UserDTO;
import com.ubre.backend.dto.user.UpdateUserDTO;
import java.util.List;

public interface UserService {
    UserDTO getUserById(Long id);
    UserDTO getUserByEmail(String email);
    List<UserDTO> getAllUsers();
    UserDTO updateUser(Long id, UpdateUserDTO updateUserDTO);
    void deleteUser(Long id);
    void blockUser(Long id);
    void unblockUser(Long id);
}
