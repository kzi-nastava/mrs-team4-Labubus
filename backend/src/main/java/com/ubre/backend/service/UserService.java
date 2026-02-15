package com.ubre.backend.service;

import com.ubre.backend.dto.*;
import com.ubre.backend.enums.UserStatus;
import com.ubre.backend.model.User;
import org.apache.coyote.BadRequestException;
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
    void blockUser(Long id, String note);
    void unblockUser(Long id);
    String getLatestBlockNote(Long userId);
    UserStatsDto getUserStats(Long id);
    void changePassword(PasswordChangeDto passwordChangeDto);
    void activateUser(Long id);
    void requestProfileChange(ProfileChangeDto profileChangeDto);
    List<ProfileChangeDto> getAllProfileChangeRequests();
    void sendPassengerRequest(Long userId, String passengerEmail);
    Resource getAvatar(Long userId);
    UserDto registerUser(UserRegistrationDto registrationDto);
    void uploadAvatar(Long userId, MultipartFile avatar);
    UserDto createAdmin(UserDto adminDto);

    List<UserDto> getUsersByFullName(String fullName);
    void recordUserStatus(Long userId, UserStatus status);
}
