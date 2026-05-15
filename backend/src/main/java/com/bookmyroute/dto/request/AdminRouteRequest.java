package com.bookmyroute.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AdminRouteRequest {

    @NotBlank
    @Size(max = 100)
    private String origin;

    @NotBlank
    @Size(max = 100)
    private String destination;

    @NotNull
    @Min(1)
    private Integer distanceKm;

    @NotNull
    @Min(1)
    private Integer durationMins;

    public AdminRouteRequest() {}

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public Integer getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Integer distanceKm) { this.distanceKm = distanceKm; }
    public Integer getDurationMins() { return durationMins; }
    public void setDurationMins(Integer durationMins) { this.durationMins = durationMins; }
}
