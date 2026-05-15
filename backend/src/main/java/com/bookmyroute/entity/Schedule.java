package com.bookmyroute.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "schedules")
@EntityListeners(AuditingEntityListener.class)
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bus_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_sched_bus"))
    private Bus bus;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "route_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_sched_route"))
    private Route route;

    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;

    @Column(name = "arrival_time", nullable = false)
    private LocalDateTime arrivalTime;

    @Column(name = "base_fare", nullable = false, precision = 10, scale = 2)
    private BigDecimal baseFare;

    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings = new ArrayList<>();

    public Schedule() {}

    public Schedule(Long id, Bus bus, Route route, LocalDateTime departureTime,
                    LocalDateTime arrivalTime, BigDecimal baseFare, Integer availableSeats,
                    Boolean isActive, LocalDateTime createdAt, List<Booking> bookings) {
        this.id = id; this.bus = bus; this.route = route;
        this.departureTime = departureTime; this.arrivalTime = arrivalTime;
        this.baseFare = baseFare; this.availableSeats = availableSeats;
        this.isActive = isActive; this.createdAt = createdAt; this.bookings = bookings;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Bus bus;
        private Route route;
        private LocalDateTime departureTime;
        private LocalDateTime arrivalTime;
        private BigDecimal baseFare;
        private Integer availableSeats;
        private Boolean isActive = true;
        private LocalDateTime createdAt;
        private List<Booking> bookings = new ArrayList<>();

        public Builder id(Long id) { this.id = id; return this; }
        public Builder bus(Bus bus) { this.bus = bus; return this; }
        public Builder route(Route route) { this.route = route; return this; }
        public Builder departureTime(LocalDateTime departureTime) { this.departureTime = departureTime; return this; }
        public Builder arrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime; return this; }
        public Builder baseFare(BigDecimal baseFare) { this.baseFare = baseFare; return this; }
        public Builder availableSeats(Integer availableSeats) { this.availableSeats = availableSeats; return this; }
        public Builder isActive(Boolean isActive) { this.isActive = isActive; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder bookings(List<Booking> bookings) { this.bookings = bookings; return this; }

        public Schedule build() {
            Schedule s = new Schedule();
            s.id = this.id; s.bus = this.bus; s.route = this.route;
            s.departureTime = this.departureTime; s.arrivalTime = this.arrivalTime;
            s.baseFare = this.baseFare; s.availableSeats = this.availableSeats;
            s.isActive = this.isActive; s.createdAt = this.createdAt; s.bookings = this.bookings;
            return s;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Bus getBus() { return bus; }
    public void setBus(Bus bus) { this.bus = bus; }
    public Route getRoute() { return route; }
    public void setRoute(Route route) { this.route = route; }
    public LocalDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }
    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime; }
    public BigDecimal getBaseFare() { return baseFare; }
    public void setBaseFare(BigDecimal baseFare) { this.baseFare = baseFare; }
    public Integer getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(Integer availableSeats) { this.availableSeats = availableSeats; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }
}
