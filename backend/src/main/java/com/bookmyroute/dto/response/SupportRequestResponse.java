package com.bookmyroute.dto.response;

import com.bookmyroute.enums.SupportCategory;
import com.bookmyroute.enums.SupportStatus;

import java.time.LocalDateTime;

public class SupportRequestResponse {
    private Long id;
    private String ticketRef;
    private SupportCategory category;
    private SupportStatus status;
    private String subject;
    private String message;
    private String bookingRef;
    private String contactName;
    private String contactEmail;
    private String contactPhone;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTicketRef() { return ticketRef; }
    public void setTicketRef(String ticketRef) { this.ticketRef = ticketRef; }
    public SupportCategory getCategory() { return category; }
    public void setCategory(SupportCategory category) { this.category = category; }
    public SupportStatus getStatus() { return status; }
    public void setStatus(SupportStatus status) { this.status = status; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getBookingRef() { return bookingRef; }
    public void setBookingRef(String bookingRef) { this.bookingRef = bookingRef; }
    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
