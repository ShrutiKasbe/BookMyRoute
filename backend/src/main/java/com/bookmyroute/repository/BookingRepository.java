package com.bookmyroute.repository;

import com.bookmyroute.entity.Booking;
import com.bookmyroute.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByUserId(Long userId);
    Optional<Booking> findByBookingRef(String bookingRef);
    List<Booking> findAllByStatus(BookingStatus status);
    List<Booking> findAllByUserIdAndStatus(Long userId, BookingStatus status);

    @Query("""
        SELECT DISTINCT b FROM Booking b
        JOIN FETCH b.user u
        JOIN FETCH b.schedule s
        JOIN FETCH s.route
        JOIN FETCH s.bus
        LEFT JOIN FETCH b.payment
        LEFT JOIN FETCH b.bookingSeats bs
        LEFT JOIN FETCH bs.seat
        WHERE b.bookingRef = :bookingRef
        """)
    Optional<Booking> findByBookingRefWithTicketDetails(@Param("bookingRef") String bookingRef);

    @Query("""
        SELECT b FROM Booking b
        JOIN FETCH b.user u
        JOIN FETCH b.schedule s
        JOIN FETCH s.route
        JOIN FETCH s.bus
        LEFT JOIN FETCH b.payment
        WHERE (:status IS NULL OR b.status = :status)
          AND (:userId IS NULL OR u.id = :userId)
          AND (:from IS NULL OR b.bookedAt >= :from)
          AND (:to IS NULL OR b.bookedAt <= :to)
        ORDER BY b.bookedAt DESC
        """)
    List<Booking> findAdminBookings(@Param("status") BookingStatus status,
                                    @Param("userId") Long userId,
                                    @Param("from") LocalDateTime from,
                                    @Param("to") LocalDateTime to);
}
