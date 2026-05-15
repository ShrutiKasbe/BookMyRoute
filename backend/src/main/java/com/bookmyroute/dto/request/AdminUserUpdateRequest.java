package com.bookmyroute.dto.request;

import com.bookmyroute.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class AdminUserUpdateRequest {

    @Size(max = 100)
    private String name;

    @Email
    @Size(max = 150)
    private String email;

    @Size(max = 15)
    private String phone;

    private Role role;

    private Boolean isActive;

    public AdminUserUpdateRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
