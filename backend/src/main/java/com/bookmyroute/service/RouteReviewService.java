package com.bookmyroute.service;

import com.bookmyroute.dto.request.RouteReviewRequest;
import com.bookmyroute.dto.request.RouteReviewUpdateRequest;
import com.bookmyroute.dto.response.PagedResponse;
import com.bookmyroute.dto.response.RouteRatingSummaryResponse;
import com.bookmyroute.dto.response.RouteReviewResponse;

public interface RouteReviewService {
    RouteReviewResponse submitReview(RouteReviewRequest request, String userEmail);
    RouteReviewResponse updateReview(Long reviewId, RouteReviewUpdateRequest request, String userEmail);
    void deleteReview(Long reviewId, String userEmail);
    PagedResponse<RouteReviewResponse> getReviewsForRoute(Long routeId, int page, int size);
    RouteRatingSummaryResponse getRouteRatingSummary(Long routeId);
    RouteReviewResponse getReviewForBooking(Long bookingId, String userEmail);
}
