package com.bookmyroute.entity;

import com.bookmyroute.enums.SupportCategory;
import com.bookmyroute.enums.SupportStatus;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "support_requests")
@EntityListeners(AuditingEntityListener.class)
public class SupportRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_support_user"))
    private User user;

    @Column(name = "ticket_ref", nullable = false, unique = true, length = 30)
    private String ticketRef;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SupportCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SupportStatus status = SupportStatus.OPEN;

    @Column(nullable = false, length = 120)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "booking_ref", length = 25)
    private String bookingRef;

    @Column(name = "contact_name", nullable = false, length = 100)
    private String contactName;

    @Column(name = "contact_email", nullable = false, length = 150)
    private String contactEmail;

    @Column(name = "contact_phone", length = 15)
    private String contactPhone;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public SupportRequest() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private User user;
        private String ticketRef;
        private SupportCategory category;
        private SupportStatus status = SupportStatus.OPEN;
        private String subject;
        private String message;
        private String bookingRef;
        private String contactName;
        private String contactEmail;
        private String contactPhone;

        public Builder user(User user) { this.user = user; return this; }
        public Builder ticketRef(String ticketRef) { this.ticketRef = ticketRef; return this; }
        public Builder category(SupportCategory category) { this.category = category; return this; }
        public Builder status(SupportStatus status) { this.status = status; return this; }
        public Builder subject(String subject) { this.subject = subject; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder bookingRef(String bookingRef) { this.bookingRef = bookingRef; return this; }
        public Builder contactName(String contactName) { this.contactName = contactName; return this; }
        public Builder contactEmail(String contactEmail) { this.contactEmail = contactEmail; return this; }
        public Builder contactPhone(String contactPhone) { this.contactPhone = contactPhone; return this; }

        public SupportRequest build() {
            SupportRequest request = new SupportRequest();
            request.user = this.user;
            request.ticketRef = this.ticketRef;
            request.category = this.category;
            request.status = this.status;
            request.subject = this.subject;
            request.message = this.message;
            request.bookingRef = this.bookingRef;
            request.contactName = this.contactName;
            request.contactEmail = this.contactEmail;
            request.contactPhone = this.contactPhone;
            return request;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
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
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
