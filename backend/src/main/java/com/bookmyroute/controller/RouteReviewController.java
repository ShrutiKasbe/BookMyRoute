package com.bookmyroute.controller;

import com.bookmyroute.dto.request.RouteReviewRequest;
import com.bookmyroute.dto.request.RouteReviewUpdateRequest;
import com.bookmyroute.dto.response.ApiResponse;
import com.bookmyroute.dto.response.PagedResponse;
import com.bookmyroute.dto.response.RouteRatingSummaryResponse;
import com.bookmyroute.dto.response.RouteReviewResponse;
import com.bookmyroute.service.RouteReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
public class RouteReviewController {

    private final RouteReviewService routeReviewService;

    public RouteReviewController(RouteReviewService routeReviewService) {
        this.routeReviewService = routeReviewService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RouteReviewResponse>> submitReview(
            @Valid @RequestBody RouteReviewRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        routeReviewService.submitReview(request, userDetails.getUsername()),
                        "Review submitted"));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<RouteReviewResponse>> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody RouteReviewUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                routeReviewService.updateReview(reviewId, request, userDetails.getUsername()),
                "Review updated"));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal UserDetails userDetails) {
        routeReviewService.deleteReview(reviewId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(null, "Review deleted"));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ApiResponse<RouteReviewResponse>> getReviewForBooking(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                routeReviewService.getReviewForBooking(bookingId, userDetails.getUsername())));
    }

    @GetMapping("/routes/{routeId}")
    public ResponseEntity<ApiResponse<PagedResponse<RouteReviewResponse>>> getRouteReviews(
            @PathVariable Long routeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                routeReviewService.getReviewsForRoute(routeId, page, size)));
    }

    @GetMapping("/routes/{routeId}/summary")
    public ResponseEntity<ApiResponse<RouteRatingSummaryResponse>> getRouteRatingSummary(
            @PathVariable Long routeId) {
        return ResponseEntity.ok(ApiResponse.success(routeReviewService.getRouteRatingSummary(routeId)));
    }
}
