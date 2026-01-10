package com.ubre.backend.repository;

import com.ubre.backend.model.Review;
import com.ubre.backend.model.Driver;
import com.ubre.backend.model.Ride;
import com.ubre.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByRide(Ride ride);
    List<Review> findByDriver(Driver driver);
    List<Review> findByUser(User user);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.driver = :driver")
    Double getAverageDriverRating(Driver driver);
}
