package com.bookmyroute.service.impl;

import com.bookmyroute.dto.request.SupportRequestCreate;
import com.bookmyroute.dto.response.SupportRequestResponse;
import com.bookmyroute.entity.SupportRequest;
import com.bookmyroute.entity.User;
import com.bookmyroute.enums.SupportStatus;
import com.bookmyroute.exception.ResourceNotFoundException;
import com.bookmyroute.repository.SupportRequestRepository;
import com.bookmyroute.repository.UserRepository;
import com.bookmyroute.service.SupportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class SupportServiceImpl implements SupportService {

    private final SupportRequestRepository supportRequestRepository;
    private final UserRepository userRepository;

    public SupportServiceImpl(SupportRequestRepository supportRequestRepository,
                              UserRepository userRepository) {
        this.supportRequestRepository = supportRequestRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public SupportRequestResponse createRequest(SupportRequestCreate request, String userEmail) {
        Optional<User> user = userEmail == null ? Optional.empty() : userRepository.findByEmail(userEmail);

        SupportRequest supportRequest = SupportRequest.builder()
                .user(user.orElse(null))
                .ticketRef(generateTicketRef())
                .category(request.getCategory())
                .status(SupportStatus.OPEN)
                .subject(clean(request.getSubject()))
                .message(clean(request.getMessage()))
                .bookingRef(blankToNull(request.getBookingRef()))
                .contactName(clean(request.getContactName()))
                .contactEmail(clean(request.getContactEmail()))
                .contactPhone(blankToNull(request.getContactPhone()))
                .build();

        return toResponse(supportRequestRepository.save(supportRequest));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupportRequestResponse> getMyRequests(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return supportRequestRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupportRequestResponse> getAllRequests() {
        return supportRequestRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private String generateTicketRef() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String ticketRef;
        do {
            int suffix = ThreadLocalRandom.current().nextInt(10000, 100000);
            ticketRef = "SUP-" + date + "-" + suffix;
        } while (supportRequestRepository.findByTicketRef(ticketRef).isPresent());
        return ticketRef;
    }

    private String clean(String value) {
        return value == null ? null : value.trim();
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private SupportRequestResponse toResponse(SupportRequest request) {
        SupportRequestResponse response = new SupportRequestResponse();
        response.setId(request.getId());
        response.setTicketRef(request.getTicketRef());
        response.setCategory(request.getCategory());
        response.setStatus(request.getStatus());
        response.setSubject(request.getSubject());
        response.setMessage(request.getMessage());
        response.setBookingRef(request.getBookingRef());
        response.setContactName(request.getContactName());
        response.setContactEmail(request.getContactEmail());
        response.setContactPhone(request.getContactPhone());
        response.setCreatedAt(request.getCreatedAt());
        return response;
    }
}
