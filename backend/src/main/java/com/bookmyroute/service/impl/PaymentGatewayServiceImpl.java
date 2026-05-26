package com.bookmyroute.service.impl;

import com.bookmyroute.dto.request.BookingRequest;
import com.bookmyroute.dto.request.PaymentOrderRequest;
import com.bookmyroute.dto.request.PaymentVerifyRequest;
import com.bookmyroute.dto.response.BookingResponse;
import com.bookmyroute.dto.response.EmailDeliveryResponse;
import com.bookmyroute.dto.response.PaymentOrderResponse;
import com.bookmyroute.entity.*;
import com.bookmyroute.enums.BookingStatus;
import com.bookmyroute.enums.PaymentStatus;
import com.bookmyroute.exception.BusinessException;
import com.bookmyroute.exception.ResourceNotFoundException;
import com.bookmyroute.repository.*;
import com.bookmyroute.service.EmailService;
import com.bookmyroute.service.PaymentGatewayService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PaymentGatewayServiceImpl implements PaymentGatewayService {

    private static final AtomicLong SEQ = new AtomicLong(1);

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Value("${razorpay.currency:INR}")
    private String currency;

    @Value("${razorpay.company.name:BookMyRoute}")
    private String companyName;

    private final RazorpayClient razorpayClient;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final RouteReviewRepository routeReviewRepository;
    private final EmailService emailService;

    public PaymentGatewayServiceImpl(RazorpayClient razorpayClient,
                                     UserRepository userRepository,
                                     ScheduleRepository scheduleRepository,
                                     SeatRepository seatRepository,
                                     BookingRepository bookingRepository,
                                     PaymentRepository paymentRepository,
                                     RouteReviewRepository routeReviewRepository,
                                     EmailService emailService) {
        this.razorpayClient = razorpayClient;
        this.userRepository = userRepository;
        this.scheduleRepository = scheduleRepository;
        this.seatRepository = seatRepository;
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.routeReviewRepository = routeReviewRepository;
        this.emailService = emailService;
    }

    // ── 1. Create Razorpay Order ──────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public PaymentOrderResponse createOrder(PaymentOrderRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Schedule schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", request.getScheduleId()));

        if (!schedule.getIsActive()) {
            throw new BusinessException("Schedule is no longer active");
        }
        if (schedule.getAvailableSeats() < request.getPassengers().size()) {
            throw new BusinessException("Not enough seats available");
        }

        // Calculate amount
        BigDecimal total = schedule.getBaseFare()
                .multiply(BigDecimal.valueOf(request.getPassengers().size()));

        // Eagerly read lazy fields while still inside the transaction/session
        String origin      = schedule.getRoute().getOrigin();
        String destination = schedule.getRoute().getDestination();
        String custName    = user.getName();
        String custEmail   = user.getEmail();
        String custPhone   = user.getPhone() != null ? user.getPhone() : "";

        // Razorpay expects amount in paise (1 INR = 100 paise)
        long amountInPaise = total.multiply(BigDecimal.valueOf(100)).longValue();

        try {
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", currency);
            orderRequest.put("receipt", "BMR-" + System.currentTimeMillis());
            orderRequest.put("payment_capture", 1); // auto-capture

            Order order = razorpayClient.orders.create(orderRequest);
            String orderId = order.get("id");

            return PaymentOrderResponse.builder()
                    .orderId(orderId)
                    .amount(total)
                    .currency(currency)
                    .keyId(razorpayKeyId)
                    .companyName(companyName)
                    .customerName(custName)
                    .customerEmail(custEmail)
                    .customerPhone(custPhone)
                    .description(origin + " → " + destination)
                    .build();

        } catch (RazorpayException e) {
            throw new BusinessException("Payment gateway error: " + e.getMessage());
        }
    }

    // ── 2. Verify Signature & Confirm Booking ─────────────────────────────

    @Override
    @Transactional
    public BookingResponse verifyAndConfirm(PaymentVerifyRequest request, String userEmail) {

        // 2a. Verify Razorpay signature
        if (!verifySignature(request.getRazorpayOrderId(), request.getRazorpayPaymentId(),
                request.getRazorpaySignature())) {
            throw new BusinessException("Payment verification failed – invalid signature");
        }

        // 2b. Load user + schedule
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Schedule schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", request.getScheduleId()));

        if (!schedule.getIsActive()) {
            throw new BusinessException("Schedule is no longer active");
        }
        if (schedule.getAvailableSeats() < request.getPassengers().size()) {
            throw new BusinessException("Not enough seats available");
        }

        // 2c. Build booking seats
        List<BookingSeat> bookingSeats = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (BookingRequest.PassengerSeat ps : request.getPassengers()) {
            Seat seat = seatRepository.findById(ps.getSeatId())
                    .orElseThrow(() -> new ResourceNotFoundException("Seat", ps.getSeatId()));

            BookingSeat bs = BookingSeat.builder()
                    .seat(seat)
                    .passengerName(ps.getPassengerName())
                    .passengerAge(ps.getPassengerAge())
                    .fare(schedule.getBaseFare())
                    .build();
            bookingSeats.add(bs);
            total = total.add(schedule.getBaseFare());
        }

        // 2d. Persist booking
        Booking booking = Booking.builder()
                .user(user)
                .schedule(schedule)
                .bookingRef(generateRef())
                .totalAmount(total)
                .status(BookingStatus.CONFIRMED)
                .build();

        bookingSeats.forEach(bs -> bs.setBooking(booking));
        booking.setBookingSeats(bookingSeats);

        // 2e. Persist payment with real Razorpay transaction id
        Payment payment = Payment.builder()
                .booking(booking)
                .paymentMethod(request.getPaymentMethod())
                .transactionId(request.getRazorpayPaymentId())   // real txn id
                .amount(total)
                .status(PaymentStatus.SUCCESS)
                .paidAt(LocalDateTime.now())
                .build();
        booking.setPayment(payment);

        // 2f. Decrement seats
        schedule.setAvailableSeats(schedule.getAvailableSeats() - request.getPassengers().size());
        scheduleRepository.save(schedule);

        Booking saved = bookingRepository.save(booking);
        EmailDeliveryResponse emailDelivery = emailService.sendBookingConfirmation(saved);
        return toResponse(saved, emailDelivery);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    /**
     * Verifies the HMAC-SHA256 signature Razorpay sends after payment.
     * See: https://razorpay.com/docs/payments/payment-gateway/web-integration/standard/build-integration/#step-3-handle-the-payment-success
     */
    private boolean verifySignature(String orderId, String paymentId, String signature) {
        try {
            String payload = orderId + "|" + paymentId;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(razorpayKeySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String generated = HexFormat.of().formatHex(hash);
            return generated.equals(signature);
        } catch (Exception e) {
            return false;
        }
    }

    private String generateRef() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "BMR-" + date + "-" + String.format("%05d", SEQ.getAndIncrement());
    }

    private BookingResponse toResponse(Booking b, EmailDeliveryResponse emailDelivery) {
        List<BookingResponse.SeatDetail> seats = b.getBookingSeats().stream()
                .map(bs -> BookingResponse.SeatDetail.builder()
                        .seatNumber(bs.getSeat().getSeatNumber())
                        .seatType(bs.getSeat().getSeatType())
                        .passengerName(bs.getPassengerName())
                        .passengerAge(bs.getPassengerAge())
                        .fare(bs.getFare())
                        .build())
                .toList();

        Payment pay = b.getPayment();
        RouteReview review = routeReviewRepository.findByBookingId(b.getId()).orElse(null);

        return BookingResponse.builder()
                .bookingId(b.getId())
                .bookingRef(b.getBookingRef())
                .routeId(b.getSchedule().getRoute().getId())
                .customerName(b.getUser().getName())
                .customerEmail(b.getUser().getEmail())
                .origin(b.getSchedule().getRoute().getOrigin())
                .destination(b.getSchedule().getRoute().getDestination())
                .departureTime(b.getSchedule().getDepartureTime())
                .arrivalTime(b.getSchedule().getArrivalTime())
                .busName(b.getSchedule().getBus().getBusName())
                .totalAmount(b.getTotalAmount())
                .bookingStatus(b.getStatus())
                .paymentStatus(pay != null ? pay.getStatus() : null)
                .paymentMethod(pay != null ? pay.getPaymentMethod() : null)
                .bookedAt(b.getBookedAt())
                .seats(seats)
                .canReview(b.getStatus() == BookingStatus.COMPLETED && review == null)
                .reviewed(review != null)
                .reviewId(review != null ? review.getReviewId() : null)
                .notificationEmailSent(emailDelivery != null ? emailDelivery.isSent() : null)
                .notificationEmailMessage(emailDelivery != null ? emailDelivery.getMessage() : null)
                .build();
    }
}