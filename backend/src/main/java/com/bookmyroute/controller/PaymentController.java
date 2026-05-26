package com.bookmyroute.controller;

import com.bookmyroute.dto.request.PaymentOrderRequest;
import com.bookmyroute.dto.request.PaymentVerifyRequest;
import com.bookmyroute.dto.response.ApiResponse;
import com.bookmyroute.dto.response.BookingResponse;
import com.bookmyroute.dto.response.PaymentOrderResponse;
import com.bookmyroute.service.PaymentGatewayService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentGatewayService paymentGatewayService;

    public PaymentController(PaymentGatewayService paymentGatewayService) {
        this.paymentGatewayService = paymentGatewayService;
    }

    /**
     * POST /api/payments/create-order
     *
     * Step 1: Frontend calls this to create a Razorpay order.
     * Returns the order id, amount, key and customer info needed to
     * open the Razorpay checkout popup.
     */
    @PostMapping("/create-order")
    public ResponseEntity<ApiResponse<PaymentOrderResponse>> createOrder(
            @Valid @RequestBody PaymentOrderRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        PaymentOrderResponse response = paymentGatewayService.createOrder(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Razorpay order created"));
    }

    /**
     * POST /api/payments/verify
     *
     * Step 2: After Razorpay calls the frontend success handler, the frontend
     * posts the payment id + signature here for server-side verification.
     * On success the booking is confirmed and a confirmation email is sent.
     */
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<BookingResponse>> verifyAndConfirm(
            @Valid @RequestBody PaymentVerifyRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        BookingResponse booking = paymentGatewayService.verifyAndConfirm(request, userDetails.getUsername());
        String message = Boolean.TRUE.equals(booking.getNotificationEmailSent())
                ? "Payment verified, booking confirmed and confirmation email sent"
                : "Payment verified and booking confirmed. Email: " + booking.getNotificationEmailMessage();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(booking, message));
    }
}