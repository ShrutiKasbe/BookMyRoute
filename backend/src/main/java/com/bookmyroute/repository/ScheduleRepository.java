package com.bookmyroute.repository;

import com.bookmyroute.entity.Schedule;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("""
        SELECT s FROM Schedule s
        JOIN FETCH s.route r
        JOIN FETCH s.bus b
        WHERE r.origin     = :origin
          AND r.destination = :destination
          AND s.departureTime BETWEEN :from AND :to
          AND s.isActive = true
          AND b.isActive = true
          AND s.availableSeats >= :seats
        ORDER BY s.departureTime
        """)
    List<Schedule> searchSchedules(@Param("origin")      String origin,
                                   @Param("destination") String destination,
                                   @Param("from")        LocalDateTime from,
                                   @Param("to")          LocalDateTime to,
                                   @Param("seats")       int seats);

    List<Schedule> findAllByBusId(Long busId);

    long countByIsActiveTrue();

    @Query("""
        SELECT s FROM Schedule s
        JOIN FETCH s.bus
        JOIN FETCH s.route
        WHERE (:active IS NULL OR s.isActive = :active)
        ORDER BY s.departureTime DESC
        """)
    List<Schedule> findAdminSchedules(@Param("active") Boolean active);

    @Query("""
        SELECT s FROM Schedule s
        JOIN FETCH s.bus b
        JOIN FETCH s.route r
        WHERE s.isActive = true
          AND b.isActive = true
          AND s.departureTime >= :from
        ORDER BY s.departureTime
        """)
    List<Schedule> findUpcomingActiveSchedules(@Param("from") LocalDateTime from, Pageable pageable);
}
