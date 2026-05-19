package com.bookmyroute.repository;

import com.bookmyroute.entity.SupportRequest;
import com.bookmyroute.enums.SupportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupportRequestRepository extends JpaRepository<SupportRequest, Long> {
    Optional<SupportRequest> findByTicketRef(String ticketRef);
    List<SupportRequest> findAllByUserIdOrderByCreatedAtDesc(Long userId);
    List<SupportRequest> findAllByStatusOrderByCreatedAtDesc(SupportStatus status);
    List<SupportRequest> findAllByOrderByCreatedAtDesc();
}
