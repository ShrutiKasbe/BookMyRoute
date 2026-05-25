package com.bookmyroute.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "route_reviews",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_route_review_booking", columnNames = "booking_id")
        },
        indexes = {
                @Index(name = "idx_route_reviews_route_created", columnList = "route_id, created_at"),
                @Index(name = "idx_route_reviews_user", columnList = "user_id")
        }
)
@EntityListeners(AuditingEntityListener.class)
public class RouteReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_review_booking"))
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "route_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_review_route"))
    private Route route;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_review_user"))
    private User user;

    @Column(nullable = false)
    private Integer rating;

    @Column(length = 1000)
    private String comment;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public RouteReview() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Booking booking;
        private Route route;
        private User user;
        private Integer rating;
        private String comment;

        public Builder booking(Booking booking) { this.booking = booking; return this; }
        public Builder route(Route route) { this.route = route; return this; }
        public Builder user(User user) { this.user = user; return this; }
        public Builder rating(Integer rating) { this.rating = rating; return this; }
        public Builder comment(String comment) { this.comment = comment; return this; }

        public RouteReview build() {
            RouteReview review = new RouteReview();
            review.booking = this.booking;
            review.route = this.route;
            review.user = this.user;
            review.rating = this.rating;
            review.comment = this.comment;
            return review;
        }
    }

    public Long getReviewId() { return reviewId; }
    public void setReviewId(Long reviewId) { this.reviewId = reviewId; }
    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }
    public Route getRoute() { return route; }
    public void setRoute(Route route) { this.route = route; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
