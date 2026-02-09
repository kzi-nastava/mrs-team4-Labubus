package com.ubre.backend.repository;

import com.ubre.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    User findUserByEmail(String email);

    @Query("SELECT u FROM User u WHERE CONCAT(u.name, ' ', u.surname) LIKE  CONCAT('%', :fullName, '%')")
    List<User> findByFullName(@Param("fullName") String fullName);
}
