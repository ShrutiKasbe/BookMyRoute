package com.bookmyroute.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "routes")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String origin;

    @Column(nullable = false, length = 100)
    private String destination;

    @Column(name = "distance_km", nullable = false)
    private Integer distanceKm;

    @Column(name = "duration_mins", nullable = false)
    private Integer durationMins;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Schedule> schedules = new ArrayList<>();

    public Route() {}

    public Route(Long id, String origin, String destination, Integer distanceKm,
                 Integer durationMins, List<Schedule> schedules) {
        this.id = id; this.origin = origin; this.destination = destination;
        this.distanceKm = distanceKm; this.durationMins = durationMins;
        this.schedules = schedules;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String origin;
        private String destination;
        private Integer distanceKm;
        private Integer durationMins;
        private List<Schedule> schedules = new ArrayList<>();

        public Builder id(Long id) { this.id = id; return this; }
        public Builder origin(String origin) { this.origin = origin; return this; }
        public Builder destination(String destination) { this.destination = destination; return this; }
        public Builder distanceKm(Integer distanceKm) { this.distanceKm = distanceKm; return this; }
        public Builder durationMins(Integer durationMins) { this.durationMins = durationMins; return this; }
        public Builder schedules(List<Schedule> schedules) { this.schedules = schedules; return this; }

        public Route build() {
            Route r = new Route();
            r.id = this.id; r.origin = this.origin; r.destination = this.destination;
            r.distanceKm = this.distanceKm; r.durationMins = this.durationMins;
            r.schedules = this.schedules;
            return r;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public Integer getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Integer distanceKm) { this.distanceKm = distanceKm; }
    public Integer getDurationMins() { return durationMins; }
    public void setDurationMins(Integer durationMins) { this.durationMins = durationMins; }
    public List<Schedule> getSchedules() { return schedules; }
    public void setSchedules(List<Schedule> schedules) { this.schedules = schedules; }
}
