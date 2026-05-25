package com.bookmyroute.service.impl;

import com.bookmyroute.dto.request.RouteReviewRequest;
import com.bookmyroute.dto.request.RouteReviewUpdateRequest;
import com.bookmyroute.dto.response.PagedResponse;
import com.bookmyroute.dto.response.RouteRatingSummaryResponse;
import com.bookmyroute.dto.response.RouteReviewResponse;
import com.bookmyroute.entity.Booking;
import com.bookmyroute.entity.RouteReview;
import com.bookmyroute.entity.User;
import com.bookmyroute.enums.BookingStatus;
import com.bookmyroute.exception.BusinessException;
import com.bookmyroute.exception.ResourceNotFoundException;
import com.bookmyroute.repository.BookingRepository;
import com.bookmyroute.repository.RouteRepository;
import com.bookmyroute.repository.RouteReviewRepository;
import com.bookmyroute.repository.UserRepository;
import com.bookmyroute.service.RouteReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RouteReviewServiceImpl implements RouteReviewService {

    private final RouteReviewRepository routeReviewRepository;
    private final BookingRepository bookingRepository;
    private final RouteRepository routeRepository;
    private final UserRepository userRepository;

    public RouteReviewServiceImpl(RouteReviewRepository routeReviewRepository,
                                  BookingRepository bookingRepository,
                                  RouteRepository routeRepository,
                                  UserRepository userRepository) {
        this.routeReviewRepository = routeReviewRepository;
        this.bookingRepository = bookingRepository;
        this.routeRepository = routeRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public RouteReviewResponse submitReview(RouteReviewRequest request, String userEmail) {
        User user = findUser(userEmail);
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking", request.getBookingId()));

        validateReviewEligibility(booking, user);
        if (routeReviewRepository.existsByBookingId(booking.getId())) {
            throw new BusinessException("A review has already been submitted for this booking");
        }

        RouteReview review = RouteReview.builder()
                .booking(booking)
                .route(booking.getSchedule().getRoute())
                .user(user)
                .rating(request.getRating())
                .comment(cleanComment(request.getComment()))
                .build();

        return toResponse(routeReviewRepository.save(review));
    }

    @Override
    @Transactional
    public RouteReviewResponse updateReview(Long reviewId, RouteReviewUpdateRequest request, String userEmail) {
        RouteReview review = getOwnedReview(reviewId, userEmail);
        review.setRating(request.getRating());
        review.setComment(cleanComment(request.getComment()));
        return toResponse(routeReviewRepository.save(review));
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId, String userEmail) {
        RouteReview review = getOwnedReview(reviewId, userEmail);
        routeReviewRepository.delete(review);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<RouteReviewResponse> getReviewsForRoute(Long routeId, int page, int size) {
        if (!routeRepository.existsById(routeId)) {
            throw new ResourceNotFoundException("Route", routeId);
        }

        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 20);
        Pageable pageable = PageRequest.of(safePage, safeSize);
        Page<RouteReview> reviews = routeReviewRepository.findAllByRouteIdOrderByCreatedAtDesc(routeId, pageable);

        return new PagedResponse<>(
                reviews.getContent().stream().map(this::toResponse).toList(),
                reviews.getNumber(),
                reviews.getSize(),
                reviews.getTotalElements(),
                reviews.getTotalPages(),
                reviews.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public RouteRatingSummaryResponse getRouteRatingSummary(Long routeId) {
        if (!routeRepository.existsById(routeId)) {
            throw new ResourceNotFoundException("Route", routeId);
        }
        return buildSummary(routeId);
    }

    @Override
    @Transactional(readOnly = true)
    public RouteReviewResponse getReviewForBooking(Long bookingId, String userEmail) {
        RouteReview review = routeReviewRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found for booking: " + bookingId));
        if (!review.getUser().getEmail().equals(userEmail)) {
            throw new BusinessException("Access denied to this review");
        }
        return toResponse(review);
    }

    private RouteReview getOwnedReview(Long reviewId, String userEmail) {
        RouteReview review = routeReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", reviewId));
        if (!review.getUser().getEmail().equals(userEmail)) {
            throw new BusinessException("Access denied to this review");
        }
        return review;
    }

    private User findUser(String userEmail) {
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void validateReviewEligibility(Booking booking, User user) {
        if (!booking.getUser().getId().equals(user.getId())) {
            throw new BusinessException("Access denied to this booking");
        }
        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new BusinessException("Only completed journeys can be reviewed");
        }
    }

    private String cleanComment(String comment) {
        if (comment == null) {
            return null;
        }
        String trimmed = comment.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private RouteRatingSummaryResponse buildSummary(Long routeId) {
        long count = routeReviewRepository.countByRouteId(routeId);
        double average = count == 0 ? 0.0 : routeReviewRepository.getAverageRatingByRouteId(routeId);
        double rounded = Math.round(average * 10.0) / 10.0;
        return new RouteRatingSummaryResponse(routeId, rounded, count);
    }

    private RouteReviewResponse toResponse(RouteReview review) {
        RouteReviewResponse response = new RouteReviewResponse();
        response.setReviewId(review.getReviewId());
        response.setBookingId(review.getBooking().getId());
        response.setRouteId(review.getRoute().getId());
        response.setUserId(review.getUser().getId());
        response.setReviewerName(review.getUser().getName());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setCreatedAt(review.getCreatedAt());
        response.setUpdatedAt(review.getUpdatedAt());
        return response;
    }
}
