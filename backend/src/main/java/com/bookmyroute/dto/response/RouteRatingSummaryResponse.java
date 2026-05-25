package com.bookmyroute.dto.response;

public class RouteRatingSummaryResponse {
    private Long routeId;
    private double averageRating;
    private long reviewCount;

    public RouteRatingSummaryResponse() {}

    public RouteRatingSummaryResponse(Long routeId, double averageRating, long reviewCount) {
        this.routeId = routeId;
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
    }

    public Long getRouteId() { return routeId; }
    public void setRouteId(Long routeId) { this.routeId = routeId; }
    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
    public long getReviewCount() { return reviewCount; }
    public void setReviewCount(long reviewCount) { this.reviewCount = reviewCount; }
}
