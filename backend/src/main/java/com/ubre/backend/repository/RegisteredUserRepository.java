package com.ubre.backend.repository;

import com.ubre.backend.model.RegisteredUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RegisteredUserRepository extends JpaRepository<RegisteredUser, Long> {
    Optional<RegisteredUser> findByEmail(String email);
    Optional<RegisteredUser> findByActivationToken(String token);
}
