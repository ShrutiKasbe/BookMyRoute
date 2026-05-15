package com.bookmyroute.dto.response;

import java.time.LocalDateTime;

public class ChatbotResponse {

    private String reply;
    private String provider;
    private LocalDateTime answeredAt;

    public ChatbotResponse() {}

    public ChatbotResponse(String reply, String provider, LocalDateTime answeredAt) {
        this.reply = reply;
        this.provider = provider;
        this.answeredAt = answeredAt;
    }

    public static ChatbotResponse of(String reply, String provider) {
        return new ChatbotResponse(reply, provider, LocalDateTime.now());
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public LocalDateTime getAnsweredAt() {
        return answeredAt;
    }

    public void setAnsweredAt(LocalDateTime answeredAt) {
        this.answeredAt = answeredAt;
    }
}
