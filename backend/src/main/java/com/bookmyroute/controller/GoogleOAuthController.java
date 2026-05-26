package com.bookmyroute.controller;

import com.bookmyroute.dto.request.GoogleOAuthRequest;
import com.bookmyroute.dto.response.ApiResponse;
import com.bookmyroute.dto.response.AuthResponse;
import com.bookmyroute.service.GoogleOAuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/oauth")
public class GoogleOAuthController {

    private final GoogleOAuthService googleOAuthService;

    public GoogleOAuthController(GoogleOAuthService googleOAuthService) {
        this.googleOAuthService = googleOAuthService;
    }

    @PostMapping("/google")
    public ResponseEntity<ApiResponse<AuthResponse>> googleLogin(
            @Valid @RequestBody GoogleOAuthRequest request) {
        AuthResponse response = googleOAuthService.authenticateWithGoogle(request.getIdToken());
        return ResponseEntity.ok(ApiResponse.success(response, "Google login successful"));
    }
}