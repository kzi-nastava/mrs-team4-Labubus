package com.ubre.backend.service;

import com.ubre.backend.dto.PasswordChangeDto;
import com.ubre.backend.dto.UserStatsDto;
import com.ubre.backend.dto.UserDto;
import com.ubre.backend.dto.ProfileChangeDto;
import org.apache.coyote.Response;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    UserDto getUserById(Long id);
    UserDto getUserByEmail(String email);
    List<UserDto> getAllUsers();
    UserDto updateUser(UserDto userDto);
    void deleteUser(Long id);
    void blockUser(Long id);
    void unblockUser(Long id);
    UserStatsDto getUserStats(Long id);
    void changePassword(PasswordChangeDto passwordChangeDto);
    void activateUser(Long id);
    void requestProfileChange(ProfileChangeDto profileChangeDto);
    List<ProfileChangeDto> getAllProfileChangeRequests();
    void sendPassengerRequest(Long userId, String passengerEmail);
    Resource getAvatar(Long userId);
    void uploadAvatar(Long userId, MultipartFile avatar);
    UserDto createAdmin(UserDto adminDto);
}
