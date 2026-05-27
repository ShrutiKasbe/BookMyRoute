package com.bookmyroute.dto.response;

public class SeatUpdateMessage {

    private Long scheduleId;
    private Integer availableSeats;

    public SeatUpdateMessage() {
    }

    public SeatUpdateMessage(Long scheduleId, Integer availableSeats) {
        this.scheduleId = scheduleId;
        this.availableSeats = availableSeats;
    }
    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }
}