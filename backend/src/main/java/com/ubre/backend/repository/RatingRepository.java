package com.ubre.backend.repository;

import com.ubre.backend.model.Rating;
import com.ubre.backend.model.Driver;
import com.ubre.backend.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByRide(Ride ride);
    List<Rating> findByDriver(Driver driver);
    
    @Query("SELECT AVG(r.driverRating) FROM Rating r WHERE r.driver = :driver")
    Double getAverageDriverRating(Driver driver);
}
