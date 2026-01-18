package com.ubre.backend.repository;

import com.ubre.backend.enums.ProfileChangeStatus;
import com.ubre.backend.enums.UserStatus;
import com.ubre.backend.model.Driver;
import com.ubre.backend.model.ProfileChange;
import com.ubre.backend.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileChangeRepository extends JpaRepository<ProfileChange, Long> {
    List<ProfileChange> findByStatus(ProfileChangeStatus status);
    List<ProfileChange> findByDriverAndStatus(Driver driver, ProfileChangeStatus status);
}
