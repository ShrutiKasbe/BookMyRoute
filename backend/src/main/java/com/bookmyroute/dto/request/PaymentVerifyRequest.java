package com.bookmyroute.dto.request;

import com.bookmyroute.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public class PaymentVerifyRequest {

    /** Razorpay order id returned when order was created  */
    @NotBlank
    private String razorpayOrderId;

    /** Payment id returned by Razorpay after successful payment */
    @NotBlank
    private String razorpayPaymentId;

    /** Signature returned by Razorpay — used to verify authenticity */
    @NotBlank
    private String razorpaySignature;

    /** Same schedule and passengers as the original order request */
    @NotNull
    private Long scheduleId;

    @NotEmpty
    @Valid
    private List<BookingRequest.PassengerSeat> passengers;

    @NotNull
    private PaymentMethod paymentMethod;

    public PaymentVerifyRequest() {}

    public String getRazorpayOrderId() { return razorpayOrderId; }
    public void setRazorpayOrderId(String razorpayOrderId) { this.razorpayOrderId = razorpayOrderId; }
    public String getRazorpayPaymentId() { return razorpayPaymentId; }
    public void setRazorpayPaymentId(String razorpayPaymentId) { this.razorpayPaymentId = razorpayPaymentId; }
    public String getRazorpaySignature() { return razorpaySignature; }
    public void setRazorpaySignature(String razorpaySignature) { this.razorpaySignature = razorpaySignature; }
    public Long getScheduleId() { return scheduleId; }
    public void setScheduleId(Long scheduleId) { this.scheduleId = scheduleId; }
    public List<BookingRequest.PassengerSeat> getPassengers() { return passengers; }
    public void setPassengers(List<BookingRequest.PassengerSeat> passengers) { this.passengers = passengers; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
}