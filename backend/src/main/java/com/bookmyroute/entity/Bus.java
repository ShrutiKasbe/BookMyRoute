package com.bookmyroute.entity;

import com.bookmyroute.enums.BusType;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "buses")
@EntityListeners(AuditingEntityListener.class)
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bus_number", nullable = false, unique = true, length = 20)
    private String busNumber;

    @Column(name = "bus_name", nullable = false, length = 100)
    private String busName;

    @Enumerated(EnumType.STRING)
    @Column(name = "bus_type", nullable = false, length = 20)
    private BusType busType;

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    @Column(length = 255)
    private String amenities;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "bus", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Seat> seats = new ArrayList<>();

    @OneToMany(mappedBy = "bus", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Schedule> schedules = new ArrayList<>();

    public Bus() {}

    public Bus(Long id, String busNumber, String busName, BusType busType, Integer totalSeats,
               String amenities, Boolean isActive, LocalDateTime createdAt,
               List<Seat> seats, List<Schedule> schedules) {
        this.id = id; this.busNumber = busNumber; this.busName = busName;
        this.busType = busType; this.totalSeats = totalSeats; this.amenities = amenities;
        this.isActive = isActive; this.createdAt = createdAt;
        this.seats = seats; this.schedules = schedules;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String busNumber;
        private String busName;
        private BusType busType;
        private Integer totalSeats;
        private String amenities;
        private Boolean isActive = true;
        private LocalDateTime createdAt;
        private List<Seat> seats = new ArrayList<>();
        private List<Schedule> schedules = new ArrayList<>();

        public Builder id(Long id) { this.id = id; return this; }
        public Builder busNumber(String busNumber) { this.busNumber = busNumber; return this; }
        public Builder busName(String busName) { this.busName = busName; return this; }
        public Builder busType(BusType busType) { this.busType = busType; return this; }
        public Builder totalSeats(Integer totalSeats) { this.totalSeats = totalSeats; return this; }
        public Builder amenities(String amenities) { this.amenities = amenities; return this; }
        public Builder isActive(Boolean isActive) { this.isActive = isActive; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder seats(List<Seat> seats) { this.seats = seats; return this; }
        public Builder schedules(List<Schedule> schedules) { this.schedules = schedules; return this; }

        public Bus build() {
            Bus b = new Bus();
            b.id = this.id; b.busNumber = this.busNumber; b.busName = this.busName;
            b.busType = this.busType; b.totalSeats = this.totalSeats; b.amenities = this.amenities;
            b.isActive = this.isActive; b.createdAt = this.createdAt;
            b.seats = this.seats; b.schedules = this.schedules;
            return b;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getBusNumber() { return busNumber; }
    public void setBusNumber(String busNumber) { this.busNumber = busNumber; }
    public String getBusName() { return busName; }
    public void setBusName(String busName) { this.busName = busName; }
    public BusType getBusType() { return busType; }
    public void setBusType(BusType busType) { this.busType = busType; }
    public Integer getTotalSeats() { return totalSeats; }
    public void setTotalSeats(Integer totalSeats) { this.totalSeats = totalSeats; }
    public String getAmenities() { return amenities; }
    public void setAmenities(String amenities) { this.amenities = amenities; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<Seat> getSeats() { return seats; }
    public void setSeats(List<Seat> seats) { this.seats = seats; }
    public List<Schedule> getSchedules() { return schedules; }
    public void setSchedules(List<Schedule> schedules) { this.schedules = schedules; }
}
