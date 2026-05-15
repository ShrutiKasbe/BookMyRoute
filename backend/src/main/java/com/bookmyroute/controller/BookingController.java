package com.bookmyroute.controller;

import com.bookmyroute.dto.request.BookingRequest;
import com.bookmyroute.dto.response.ApiResponse;
import com.bookmyroute.dto.response.BookingResponse;
import com.bookmyroute.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @Valid @RequestBody BookingRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        BookingResponse response = bookingService.createBooking(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Booking confirmed"));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> myBookings(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                bookingService.getMyBookings(userDetails.getUsername())));
    }

    @GetMapping("/{bookingRef}")
    public ResponseEntity<ApiResponse<BookingResponse>> getBooking(
            @PathVariable String bookingRef,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                bookingService.getBookingByRef(bookingRef, userDetails.getUsername())));
    }

    // Changed from @PostMapping to @PatchMapping to match REST conventions
    // and the React frontend's bookingApi.cancelBooking which uses PATCH
    @PatchMapping("/{bookingRef}/cancel")
    public ResponseEntity<ApiResponse<BookingResponse>> cancelBooking(
            @PathVariable String bookingRef,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                bookingService.cancelBooking(bookingRef, userDetails.getUsername()),
                "Booking cancelled and refund initiated"));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getAllBookings() {
        return ResponseEntity.ok(ApiResponse.success(bookingService.getAllBookings()));
    }
}
