package com.bookmyroute.controller;

import com.bookmyroute.dto.request.SupportRequestCreate;
import com.bookmyroute.dto.request.SupportReplyRequest;
import com.bookmyroute.dto.response.ApiResponse;
import com.bookmyroute.dto.response.SupportRequestResponse;
import com.bookmyroute.service.SupportService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/support")
public class SupportController {

    private final SupportService supportService;

    public SupportController(SupportService supportService) {
        this.supportService = supportService;
    }

    @PostMapping("/requests")
    public ResponseEntity<ApiResponse<SupportRequestResponse>> createSupportRequest(
            @Valid @RequestBody SupportRequestCreate request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails == null ? null : userDetails.getUsername();
        SupportRequestResponse response = supportService.createRequest(request, userEmail);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Support request submitted"));
    }

    @GetMapping("/requests/my")
    public ResponseEntity<ApiResponse<List<SupportRequestResponse>>> getMySupportRequests(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                supportService.getMyRequests(userDetails.getUsername())));
    }

    @GetMapping("/requests")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<SupportRequestResponse>>> getAllSupportRequests() {
        return ResponseEntity.ok(ApiResponse.success(supportService.getAllRequests()));
    }

    @PostMapping("/requests/{ticketRef}/reply")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SupportRequestResponse>> replyToSupportRequest(
            @PathVariable String ticketRef,
            @Valid @RequestBody SupportReplyRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                supportService.replyToRequest(ticketRef, request),
                "Support reply sent"));
    }
}
