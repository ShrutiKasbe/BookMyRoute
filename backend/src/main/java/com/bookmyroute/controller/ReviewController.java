package com.bookmyroute.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.bookmyroute.entity.Review;
import com.bookmyroute.repository.ReviewRepository;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin("*")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @PostMapping
    public Review addReview(@RequestBody Review review) {
        return reviewRepository.save(review);
    }

    @GetMapping("/route/{routeId}")
    public List<Review> getReviews(@PathVariable Long routeId) {
        return reviewRepository.findByRouteId(routeId);
    }

    @GetMapping("/average/{routeId}")
    public double getAverageRating(@PathVariable Long routeId) {

        List<Review> reviews = reviewRepository.findByRouteId(routeId);

        if (reviews.isEmpty()) {
            return 0;
        }

        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0);
    }
}