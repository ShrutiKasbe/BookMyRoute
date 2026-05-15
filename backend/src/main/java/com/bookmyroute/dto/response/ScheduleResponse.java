package com.bookmyroute.dto.response;

import com.bookmyroute.enums.BusType;
import com.bookmyroute.enums.SeatType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ScheduleResponse {

    public static class Search {
        private Long scheduleId;
        private String origin;
        private String destination;
        private LocalDateTime departureTime;
        private LocalDateTime arrivalTime;
        private BigDecimal baseFare;
        private int availableSeats;
        private String busName;
        private BusType busType;
        private String amenities;
        private int durationMins;

        public Search() {}

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private Long scheduleId;
            private String origin;
            private String destination;
            private LocalDateTime departureTime;
            private LocalDateTime arrivalTime;
            private BigDecimal baseFare;
            private int availableSeats;
            private String busName;
            private BusType busType;
            private String amenities;
            private int durationMins;

            public Builder scheduleId(Long scheduleId) { this.scheduleId = scheduleId; return this; }
            public Builder origin(String origin) { this.origin = origin; return this; }
            public Builder destination(String destination) { this.destination = destination; return this; }
            public Builder departureTime(LocalDateTime departureTime) { this.departureTime = departureTime; return this; }
            public Builder arrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime; return this; }
            public Builder baseFare(BigDecimal baseFare) { this.baseFare = baseFare; return this; }
            public Builder availableSeats(int availableSeats) { this.availableSeats = availableSeats; return this; }
            public Builder busName(String busName) { this.busName = busName; return this; }
            public Builder busType(BusType busType) { this.busType = busType; return this; }
            public Builder amenities(String amenities) { this.amenities = amenities; return this; }
            public Builder durationMins(int durationMins) { this.durationMins = durationMins; return this; }

            public Search build() {
                Search s = new Search();
                s.scheduleId = this.scheduleId; s.origin = this.origin; s.destination = this.destination;
                s.departureTime = this.departureTime; s.arrivalTime = this.arrivalTime;
                s.baseFare = this.baseFare; s.availableSeats = this.availableSeats;
                s.busName = this.busName; s.busType = this.busType;
                s.amenities = this.amenities; s.durationMins = this.durationMins;
                return s;
            }
        }

        public Long getScheduleId() { return scheduleId; }
        public void setScheduleId(Long scheduleId) { this.scheduleId = scheduleId; }
        public String getOrigin() { return origin; }
        public void setOrigin(String origin) { this.origin = origin; }
        public String getDestination() { return destination; }
        public void setDestination(String destination) { this.destination = destination; }
        public LocalDateTime getDepartureTime() { return departureTime; }
        public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }
        public LocalDateTime getArrivalTime() { return arrivalTime; }
        public void setArrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime; }
        public BigDecimal getBaseFare() { return baseFare; }
        public void setBaseFare(BigDecimal baseFare) { this.baseFare = baseFare; }
        public int getAvailableSeats() { return availableSeats; }
        public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }
        public String getBusName() { return busName; }
        public void setBusName(String busName) { this.busName = busName; }
        public BusType getBusType() { return busType; }
        public void setBusType(BusType busType) { this.busType = busType; }
        public String getAmenities() { return amenities; }
        public void setAmenities(String amenities) { this.amenities = amenities; }
        public int getDurationMins() { return durationMins; }
        public void setDurationMins(int durationMins) { this.durationMins = durationMins; }
    }

    public static class SeatInfo {
        private Long seatId;
        private String seatNumber;
        private SeatType seatType;

        public SeatInfo() {}

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private Long seatId;
            private String seatNumber;
            private SeatType seatType;

            public Builder seatId(Long seatId) { this.seatId = seatId; return this; }
            public Builder seatNumber(String seatNumber) { this.seatNumber = seatNumber; return this; }
            public Builder seatType(SeatType seatType) { this.seatType = seatType; return this; }

            public SeatInfo build() {
                SeatInfo si = new SeatInfo();
                si.seatId = this.seatId; si.seatNumber = this.seatNumber; si.seatType = this.seatType;
                return si;
            }
        }

        public Long getSeatId() { return seatId; }
        public void setSeatId(Long seatId) { this.seatId = seatId; }
        public String getSeatNumber() { return seatNumber; }
        public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
        public SeatType getSeatType() { return seatType; }
        public void setSeatType(SeatType seatType) { this.seatType = seatType; }
    }
}
