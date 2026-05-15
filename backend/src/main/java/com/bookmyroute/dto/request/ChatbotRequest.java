package com.bookmyroute.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChatbotRequest {

    @NotBlank(message = "Message is required")
    @Size(max = 1000, message = "Message must be 1000 characters or less")
    private String message;

    public ChatbotRequest() {}

    public ChatbotRequest(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
