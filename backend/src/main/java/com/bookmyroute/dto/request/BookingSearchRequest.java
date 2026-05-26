package com.bookmyroute.dto.request;

import com.bookmyroute.enums.BookingStatus;

import java.time.LocalDate;

public class BookingSearchRequest {
    private BookingStatus status;
    private LocalDate fromDate;
    private LocalDate toDate;
    private int page;
    private int size;
    private String sortBy;
    private String sortDir;

    public BookingSearchRequest() {}

    public BookingSearchRequest(BookingStatus status, LocalDate fromDate, LocalDate toDate,
                                int page, int size, String sortBy, String sortDir) {
        this.status = status;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.page = page;
        this.size = size;
        this.sortBy = sortBy;
        this.sortDir = sortDir;
    }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public LocalDate getFromDate() { return fromDate; }
    public void setFromDate(LocalDate fromDate) { this.fromDate = fromDate; }
    public LocalDate getToDate() { return toDate; }
    public void setToDate(LocalDate toDate) { this.toDate = toDate; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }
    public String getSortDir() { return sortDir; }
    public void setSortDir(String sortDir) { this.sortDir = sortDir; }
}
