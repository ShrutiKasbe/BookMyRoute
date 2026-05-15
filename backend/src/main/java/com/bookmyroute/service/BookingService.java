package com.bookmyroute.service;

import com.bookmyroute.dto.request.BookingRequest;
import com.bookmyroute.dto.response.BookingResponse;

import java.util.List;

public interface BookingService {
    BookingResponse createBooking(BookingRequest request, String userEmail);
    BookingResponse getBookingByRef(String bookingRef, String userEmail);
    List<BookingResponse> getMyBookings(String userEmail);
    BookingResponse cancelBooking(String bookingRef, String userEmail);
    List<BookingResponse> getAllBookings();   // admin
}
