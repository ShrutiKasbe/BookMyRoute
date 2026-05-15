package com.bookmyroute.dto.response;

import java.math.BigDecimal;

public class AdminDashboardResponse {

    private long totalUsers;
    private long activeUsers;
    private long totalBookings;
    private long confirmedBookings;
    private long cancelledBookings;
    private long activeBuses;
    private long totalRoutes;
    private long activeSchedules;
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    public AdminDashboardResponse() {}

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
    public long getActiveUsers() { return activeUsers; }
    public void setActiveUsers(long activeUsers) { this.activeUsers = activeUsers; }
    public long getTotalBookings() { return totalBookings; }
    public void setTotalBookings(long totalBookings) { this.totalBookings = totalBookings; }
    public long getConfirmedBookings() { return confirmedBookings; }
    public void setConfirmedBookings(long confirmedBookings) { this.confirmedBookings = confirmedBookings; }
    public long getCancelledBookings() { return cancelledBookings; }
    public void setCancelledBookings(long cancelledBookings) { this.cancelledBookings = cancelledBookings; }
    public long getActiveBuses() { return activeBuses; }
    public void setActiveBuses(long activeBuses) { this.activeBuses = activeBuses; }
    public long getTotalRoutes() { return totalRoutes; }
    public void setTotalRoutes(long totalRoutes) { this.totalRoutes = totalRoutes; }
    public long getActiveSchedules() { return activeSchedules; }
    public void setActiveSchedules(long activeSchedules) { this.activeSchedules = activeSchedules; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
}
