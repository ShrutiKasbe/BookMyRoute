package com.bookmyroute.service;

import com.bookmyroute.dto.response.AuthResponse;
import com.bookmyroute.entity.User;
import com.bookmyroute.enums.Role;
import com.bookmyroute.repository.UserRepository;
import com.bookmyroute.security.JwtUtils;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.UUID;

@Service
public class GoogleOAuthService {

    private static final Logger log = LoggerFactory.getLogger(GoogleOAuthService.class);

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    // Built once at startup — not on every login request
    private final GoogleIdTokenVerifier verifier;

    public GoogleOAuthService(UserRepository userRepository,
                               JwtUtils jwtUtils,
                               UserDetailsService userDetailsService,
                               @Value("${google.oauth.client-id}") String googleClientId) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;

        this.verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(googleClientId))
                .build();
    }

    @Transactional
    public AuthResponse authenticateWithGoogle(String idTokenString) {
        GoogleIdToken.Payload payload = verifyGoogleToken(idTokenString);

        String googleSub = payload.getSubject();
        String email     = payload.getEmail();
        String name      = (String) payload.get("name");
        if (name == null || name.isBlank()) name = email.split("@")[0];

        User user = userRepository.findByGoogleSub(googleSub)
                .or(() -> userRepository.findByEmail(email))
                .orElse(null);

        if (user == null) {
            User newUser = User.builder()
                    .name(name)
                    .email(email)
                    .googleSub(googleSub)
                    .passwordHash(UUID.randomUUID().toString())
                    .role(Role.PASSENGER)
                    .isActive(true)
                    .build();
            try {
                user = userRepository.save(newUser);
                log.info("Auto-registered new Google OAuth user: {}", email);
            } catch (DataIntegrityViolationException e) {
                // Concurrent request already inserted this user — fetch and proceed
                log.warn("Concurrent Google OAuth registration detected for: {}. Fetching existing user.", email);
                user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Google login failed: user conflict for " + email));
            }
        } else {
            if (user.getGoogleSub() == null) {
                user.setGoogleSub(googleSub);
                user = userRepository.save(user);
                log.info("Linked Google sub to existing account: {}", email);
            }
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtils.generateToken(userDetails);

        return AuthResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .accessToken(token)
                .tokenType("Bearer")
                .build();
    }

    private GoogleIdToken.Payload verifyGoogleToken(String idTokenString) {
        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new RuntimeException("Invalid or expired Google ID token");
            }
            GoogleIdToken.Payload payload = idToken.getPayload();
            if (!Boolean.TRUE.equals(payload.getEmailVerified())) {
                throw new RuntimeException("Google account email is not verified");
            }
            return payload;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Google token verification failed", e);
            throw new RuntimeException("Google token verification failed: " + e.getMessage());
        }
    }
}