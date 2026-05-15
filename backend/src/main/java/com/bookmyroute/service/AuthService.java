package com.bookmyroute.service;

import com.bookmyroute.dto.request.AuthRequest;
import com.bookmyroute.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(AuthRequest.Register request);
    AuthResponse login(AuthRequest.Login request);
}
