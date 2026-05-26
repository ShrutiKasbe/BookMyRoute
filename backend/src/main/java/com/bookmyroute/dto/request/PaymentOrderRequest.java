package com.bookmyroute.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public class PaymentOrderRequest {

    @NotNull
    private Long scheduleId;

    @NotEmpty
    @Valid
    private List<BookingRequest.PassengerSeat> passengers;

    public PaymentOrderRequest() {}

    public Long getScheduleId() { return scheduleId; }
    public void setScheduleId(Long scheduleId) { this.scheduleId = scheduleId; }
    public List<BookingRequest.PassengerSeat> getPassengers() { return passengers; }
    public void setPassengers(List<BookingRequest.PassengerSeat> passengers) { this.passengers = passengers; }
}