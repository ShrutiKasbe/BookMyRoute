package com.bookmyroute.service;

import com.bookmyroute.dto.request.SupportRequestCreate;
import com.bookmyroute.dto.response.SupportRequestResponse;

import java.util.List;

public interface SupportService {
    SupportRequestResponse createRequest(SupportRequestCreate request, String userEmail);
    List<SupportRequestResponse> getMyRequests(String userEmail);
    List<SupportRequestResponse> getAllRequests();
}
