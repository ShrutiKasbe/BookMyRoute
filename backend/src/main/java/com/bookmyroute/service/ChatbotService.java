package com.bookmyroute.service;

import com.bookmyroute.dto.response.ChatbotResponse;

public interface ChatbotService {
    ChatbotResponse ask(String message);
}
