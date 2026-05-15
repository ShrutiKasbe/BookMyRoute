package com.bookmyroute.repository;

import com.bookmyroute.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    List<Route> findByOriginIgnoreCaseAndDestinationIgnoreCase(String origin, String destination);
    List<Route> findByOriginIgnoreCase(String origin);
}
