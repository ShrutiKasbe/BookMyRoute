package com.bookmyroute.dto.request;

import com.bookmyroute.enums.SupportCategory;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SupportRequestCreate {

    @NotNull
    private SupportCategory category;

    @NotBlank
    @Size(max = 120)
    private String subject;

    @NotBlank
    @Size(min = 10, max = 2000)
    private String message;

    @Size(max = 25)
    private String bookingRef;

    @NotBlank
    @Size(max = 100)
    private String contactName;

    @NotBlank
    @Email
    @Size(max = 150)
    private String contactEmail;

    @Size(max = 15)
    private String contactPhone;

    public SupportCategory getCategory() { return category; }
    public void setCategory(SupportCategory category) { this.category = category; }
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
}
