package com.bookmyroute.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookmyroute.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByRouteId(Long routeId);
}