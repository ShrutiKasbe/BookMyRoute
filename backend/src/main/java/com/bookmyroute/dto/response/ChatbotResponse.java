package com.bookmyroute.dto.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChatbotResponse {

    private String reply;
    private String provider;
    private List<String> suggestions = new ArrayList<>();
    private LocalDateTime answeredAt;

    public ChatbotResponse() {}

    public ChatbotResponse(String reply, String provider, List<String> suggestions, LocalDateTime answeredAt) {
        this.reply = reply;
        this.provider = provider;
        this.suggestions = suggestions == null ? new ArrayList<>() : suggestions;
        this.answeredAt = answeredAt;
    }

    public static ChatbotResponse of(String reply, String provider) {
        return new ChatbotResponse(reply, provider, defaultSuggestions(), LocalDateTime.now());
    }

    public static ChatbotResponse of(String reply, String provider, List<String> suggestions) {
        return new ChatbotResponse(reply, provider, suggestions, LocalDateTime.now());
    }

    private static List<String> defaultSuggestions() {
        return List.of(
                "Search buses",
                "How do I cancel?",
                "Download ticket PDF"
        );
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

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions == null ? new ArrayList<>() : suggestions;
    }

    public LocalDateTime getAnsweredAt() {
        return answeredAt;
    }

    public void setAnsweredAt(LocalDateTime answeredAt) {
        this.answeredAt = answeredAt;
    }
}
