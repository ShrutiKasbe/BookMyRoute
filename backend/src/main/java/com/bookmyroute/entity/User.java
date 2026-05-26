package com.bookmyroute.entity;

import com.bookmyroute.enums.Role;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(length = 15)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.PASSENGER;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "google_sub", unique = true, length = 255)
    private String googleSub;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings = new ArrayList<>();

    public User() {}

    public User(Long id, String name, String email, String passwordHash, String phone,
                Role role, Boolean isActive, String googleSub,
                LocalDateTime createdAt, LocalDateTime updatedAt,
                List<Booking> bookings) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.phone = phone;
        this.role = role;
        this.isActive = isActive;
        this.googleSub = googleSub;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.bookings = bookings;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String name;
        private String email;
        private String passwordHash;
        private String phone;
        private Role role = Role.PASSENGER;
        private Boolean isActive = true;
        private String googleSub;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<Booking> bookings = new ArrayList<>();

        public Builder id(Long id)                      { this.id = id; return this; }
        public Builder name(String name)                { this.name = name; return this; }
        public Builder email(String email)              { this.email = email; return this; }
        public Builder passwordHash(String p)           { this.passwordHash = p; return this; }
        public Builder phone(String phone)              { this.phone = phone; return this; }
        public Builder role(Role role)                  { this.role = role; return this; }
        public Builder isActive(Boolean a)              { this.isActive = a; return this; }
        public Builder googleSub(String sub)            { this.googleSub = sub; return this; }
        public Builder createdAt(LocalDateTime t)       { this.createdAt = t; return this; }
        public Builder updatedAt(LocalDateTime t)       { this.updatedAt = t; return this; }
        public Builder bookings(List<Booking> bookings) { this.bookings = bookings; return this; }

        public User build() {
            User u = new User();
            u.id = this.id;
            u.name = this.name;
            u.email = this.email;
            u.passwordHash = this.passwordHash;
            u.phone = this.phone;
            u.role = this.role;
            u.isActive = this.isActive;
            u.googleSub = this.googleSub;
            u.createdAt = this.createdAt;
            u.updatedAt = this.updatedAt;
            u.bookings = this.bookings;
            return u;
        }
    }

    public Long getId()                        { return id; }
    public void setId(Long id)                 { this.id = id; }
    public String getName()                    { return name; }
    public void setName(String name)           { this.name = name; }
    public String getEmail()                   { return email; }
    public void setEmail(String email)         { this.email = email; }
    public String getPasswordHash()            { return passwordHash; }
    public void setPasswordHash(String p)      { this.passwordHash = p; }
    public String getPhone()                   { return phone; }
    public void setPhone(String phone)         { this.phone = phone; }
    public Role getRole()                      { return role; }
    public void setRole(Role role)             { this.role = role; }
    public Boolean getIsActive()               { return isActive; }
    public void setIsActive(Boolean a)         { this.isActive = a; }
    public String getGoogleSub()               { return googleSub; }
    public void setGoogleSub(String googleSub) { this.googleSub = googleSub; }
    public LocalDateTime getCreatedAt()        { return createdAt; }
    public void setCreatedAt(LocalDateTime t)  { this.createdAt = t; }
    public LocalDateTime getUpdatedAt()        { return updatedAt; }
    public void setUpdatedAt(LocalDateTime t)  { this.updatedAt = t; }
    public List<Booking> getBookings()         { return bookings; }
    public void setBookings(List<Booking> b)   { this.bookings = b; }
}