package com.bookmyroute.repository;

import com.bookmyroute.entity.RouteReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RouteReviewRepository extends JpaRepository<RouteReview, Long> {
    boolean existsByBookingId(Long bookingId);
    Optional<RouteReview> findByBookingId(Long bookingId);
    Page<RouteReview> findAllByRouteIdOrderByCreatedAtDesc(Long routeId, Pageable pageable);

    @Query("select coalesce(avg(r.rating), 0) from RouteReview r where r.route.id = :routeId")
    double getAverageRatingByRouteId(@Param("routeId") Long routeId);

    long countByRouteId(Long routeId);
}
