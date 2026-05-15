package com.bookmyroute.controller;

import com.bookmyroute.dto.request.ChatbotRequest;
import com.bookmyroute.dto.response.ApiResponse;
import com.bookmyroute.dto.response.ChatbotResponse;
import com.bookmyroute.service.ChatbotService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/message")
    public ResponseEntity<ApiResponse<ChatbotResponse>> message(@Valid @RequestBody ChatbotRequest request) {
        return ResponseEntity.ok(ApiResponse.success(chatbotService.ask(request.getMessage()), "Chatbot response"));
    }
}
