package com.bookmyroute.service;

import com.bookmyroute.dto.request.ChatbotRequest;
import com.bookmyroute.dto.response.ChatbotResponse;

public interface ChatbotService {
    ChatbotResponse ask(ChatbotRequest request, String userEmail);
}
