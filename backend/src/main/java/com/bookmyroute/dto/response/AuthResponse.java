package com.bookmyroute.dto.response;

import com.bookmyroute.enums.Role;

public class AuthResponse {
    private String accessToken;
    private String tokenType;
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private Role role;

    public AuthResponse() {}

    public AuthResponse(String accessToken, String tokenType, Long userId,
                        String name, String email, String phone, Role role) {
        this.accessToken = accessToken; this.tokenType = tokenType; this.userId = userId;
        this.name = name; this.email = email; this.phone = phone; this.role = role;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String accessToken;
        private String tokenType;
        private Long userId;
        private String name;
        private String email;
        private String phone;
        private Role role;

        public Builder accessToken(String accessToken) { this.accessToken = accessToken; return this; }
        public Builder tokenType(String tokenType) { this.tokenType = tokenType; return this; }
        public Builder userId(Long userId) { this.userId = userId; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder phone(String phone) { this.phone = phone; return this; }
        public Builder role(Role role) { this.role = role; return this; }

        public AuthResponse build() {
            AuthResponse r = new AuthResponse();
            r.accessToken = this.accessToken; r.tokenType = this.tokenType; r.userId = this.userId;
            r.name = this.name; r.email = this.email; r.phone = this.phone; r.role = this.role;
            return r;
        }
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
