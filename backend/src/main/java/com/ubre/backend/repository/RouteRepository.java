package com.ubre.backend.repository;

import com.ubre.backend.model.Route;
import com.ubre.backend.model.RegisteredUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    List<Route> findByUser(RegisteredUser user);
    List<Route> findByUserAndIsFavoriteTrue(RegisteredUser user);
}
