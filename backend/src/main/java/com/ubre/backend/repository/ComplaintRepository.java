package com.ubre.backend.repository;

import com.ubre.backend.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByRide(Ride ride);
    List<Complaint> findByDriver(Driver driver);
    List<Complaint> findByUser(User user);
    List<Complaint> findByUserAndDriver(User user, Driver driver);
}
