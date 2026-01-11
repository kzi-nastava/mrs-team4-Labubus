package com.ubre.backend.repository;

import com.ubre.backend.model.Complaint;
import com.ubre.backend.model.Driver;
import com.ubre.backend.model.Review;
import com.ubre.backend.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    Optional<Complaint> findByRide(Ride ride);
    List<Complaint> findByDriver(Driver driver);
}
