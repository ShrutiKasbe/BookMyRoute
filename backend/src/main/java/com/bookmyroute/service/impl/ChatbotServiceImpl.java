package com.bookmyroute.service.impl;

import com.bookmyroute.dto.response.ChatbotResponse;
import com.bookmyroute.entity.Route;
import com.bookmyroute.entity.Schedule;
import com.bookmyroute.repository.RouteRepository;
import com.bookmyroute.repository.ScheduleRepository;
import com.bookmyroute.service.ChatbotService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ChatbotServiceImpl implements ChatbotService {

    private static final DateTimeFormatter DISPLAY_TIME = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    private final RouteRepository routeRepository;
    private final ScheduleRepository scheduleRepository;
    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    public ChatbotServiceImpl(RouteRepository routeRepository,
                              ScheduleRepository scheduleRepository,
                              RestClient.Builder restClientBuilder,
                              @Value("${openai.api.key:}") String apiKey,
                              @Value("${openai.model:gpt-5.4-mini}") String model) {
        this.routeRepository = routeRepository;
        this.scheduleRepository = scheduleRepository;
        this.restClient = restClientBuilder.baseUrl("https://api.openai.com/v1").build();
        this.apiKey = apiKey;
        this.model = model;
    }

    @Override
    @Transactional(readOnly = true)
    public ChatbotResponse ask(String message) {
        String cleanMessage = message.trim();
        String context = buildBookingContext();

        if (!StringUtils.hasText(apiKey)) {
            return ChatbotResponse.of(buildLocalFallback(cleanMessage, context), "LOCAL_FALLBACK");
        }

        try {
            JsonNode response = restClient.post()
                    .uri("/responses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(headers -> headers.setBearerAuth(apiKey))
                    .body(buildOpenAiRequest(cleanMessage, context))
                    .retrieve()
                    .body(JsonNode.class);

            String reply = extractOutputText(response);
            if (!StringUtils.hasText(reply)) {
                reply = "I could not generate a clear answer right now. Please try again with your route, date, or booking question.";
            }
            return ChatbotResponse.of(reply, "OPENAI");
        } catch (RestClientException ex) {
            return ChatbotResponse.of(buildLocalFallback(cleanMessage, context), "LOCAL_FALLBACK");
        }
    }

    private Map<String, Object> buildOpenAiRequest(String message, String context) {
        String instructions = """
                You are BookMyRoute Assistant, a concise support chatbot for a bus ticket booking platform.
                Help users with routes, schedules, fares, seats, booking steps, payments, cancellations, and account questions.
                Use the provided live BookMyRoute context when answering availability questions.
                If the requested live detail is not in the context, say what information is missing and suggest using route search.
                Keep answers friendly, practical, and under 120 words.
                Do not answer unrelated questions.
                """;

        String inputText = "Live BookMyRoute context:\n" + context + "\n\nUser question: " + message;

        return Map.of(
                "model", model,
                "instructions", instructions,
                "input", List.of(Map.of(
                        "role", "user",
                        "content", List.of(Map.of(
                                "type", "input_text",
                                "text", inputText
                        ))
                )),
                "max_output_tokens", 450
        );
    }

    private String buildBookingContext() {
        List<String> lines = new ArrayList<>();
        List<Route> routes = routeRepository.findAll();
        List<Schedule> schedules = scheduleRepository.findUpcomingActiveSchedules(LocalDateTime.now(), PageRequest.of(0, 20));

        if (routes.isEmpty()) {
            lines.add("No routes are currently configured.");
        } else {
            lines.add("Available routes:");
            routes.stream()
                    .limit(30)
                    .forEach(route -> lines.add(String.format(
                            "- %s to %s, %d km, about %d minutes",
                            route.getOrigin(),
                            route.getDestination(),
                            route.getDistanceKm(),
                            route.getDurationMins()
                    )));
        }

        if (schedules.isEmpty()) {
            lines.add("No upcoming active schedules are currently available.");
        } else {
            lines.add("Upcoming active schedules:");
            schedules.forEach(schedule -> lines.add(String.format(
                    "- Schedule %d: %s to %s, %s, bus %s (%s), fare %s, %d seats available",
                    schedule.getId(),
                    schedule.getRoute().getOrigin(),
                    schedule.getRoute().getDestination(),
                    DISPLAY_TIME.format(schedule.getDepartureTime()),
                    schedule.getBus().getBusName(),
                    schedule.getBus().getBusType(),
                    schedule.getBaseFare(),
                    schedule.getAvailableSeats()
            )));
        }

        lines.add("Common flow: search buses by origin, destination, date, and seats; choose a schedule; pick seats; complete payment; keep the booking confirmation.");
        return String.join("\n", lines);
    }

    private String buildLocalFallback(String message, String context) {
        String lowerMessage = message.toLowerCase();

        if (lowerMessage.contains("route") || lowerMessage.contains("available") || lowerMessage.contains("schedule")
                || lowerMessage.contains("bus") || lowerMessage.contains("fare") || lowerMessage.contains("seat")) {
            return "I can help with route, schedule, fare, and seat questions. AI is not configured yet, but here is the latest booking context I can see:\n\n" + context;
        }

        if (lowerMessage.contains("book") || lowerMessage.contains("payment") || lowerMessage.contains("ticket")) {
            return "To book a ticket, search by origin, destination, travel date, and number of seats. Then choose a schedule, select seats, complete payment, and save the booking confirmation.";
        }

        return "I can help with BookMyRoute questions like route availability, bus timings, fares, seats, booking steps, payments, and ticket information. Please ask with your origin, destination, and travel date for the quickest help.";
    }

    private String extractOutputText(JsonNode response) {
        if (response == null) {
            return "";
        }

        JsonNode output = response.path("output");
        if (!output.isArray()) {
            return "";
        }

        StringBuilder text = new StringBuilder();
        for (JsonNode item : output) {
            JsonNode content = item.path("content");
            if (!content.isArray()) {
                continue;
            }

            for (JsonNode contentItem : content) {
                String type = contentItem.path("type").asText();
                if ("output_text".equals(type) && contentItem.hasNonNull("text")) {
                    text.append(contentItem.path("text").asText()).append("\n");
                }
            }
        }
        return text.toString().trim();
    }
}
