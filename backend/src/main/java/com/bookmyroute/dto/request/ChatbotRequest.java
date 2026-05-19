package com.bookmyroute.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;

public class ChatbotRequest {

    @NotBlank(message = "Message is required")
    @Size(max = 1000, message = "Message must be 1000 characters or less")
    private String message;

    @Valid
    @Size(max = 12, message = "History can include at most 12 messages")
    private List<HistoryMessage> history = new ArrayList<>();

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

    public List<HistoryMessage> getHistory() {
        return history;
    }

    public void setHistory(List<HistoryMessage> history) {
        this.history = history == null ? new ArrayList<>() : history;
    }

    public static class HistoryMessage {
        @NotBlank(message = "History role is required")
        @Size(max = 20, message = "History role is too long")
        private String role;

        @NotBlank(message = "History text is required")
        @Size(max = 1000, message = "History text must be 1000 characters or less")
        private String text;

        public HistoryMessage() {}

        public HistoryMessage(String role, String text) {
            this.role = role;
            this.text = text;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
