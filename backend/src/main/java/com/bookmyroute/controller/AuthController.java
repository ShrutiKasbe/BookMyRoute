package com.bookmyroute.controller;

import com.bookmyroute.dto.request.AuthRequest;
import com.bookmyroute.dto.response.ApiResponse;
import com.bookmyroute.dto.response.AuthResponse;
import com.bookmyroute.entity.User;
import com.bookmyroute.enums.Role;
import com.bookmyroute.exception.ResourceNotFoundException;
import com.bookmyroute.repository.UserRepository;
import com.bookmyroute.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody AuthRequest.Register request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Registration successful"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody AuthRequest.Login request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }

    @PostMapping("/admin/login")
    public ResponseEntity<ApiResponse<AuthResponse>> adminLogin(
            @Valid @RequestBody AuthRequest.Login request) {
        AuthResponse response = authService.login(request);
        if (response.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Only admin users can access the admin panel");
        }
        return ResponseEntity.ok(ApiResponse.success(response, "Admin login successful"));
    }

    /**
     * GET /api/auth/me
     * Returns the current logged-in user's profile.
     * Used by the React frontend to restore session on page reload.
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthResponse>> me(
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        AuthResponse response = AuthResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .tokenType("Bearer")
                .build();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
