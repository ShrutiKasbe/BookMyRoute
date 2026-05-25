package com.bookmyroute.service.impl;

import com.bookmyroute.dto.request.ChatbotRequest;
import com.bookmyroute.dto.response.ChatbotResponse;
import com.bookmyroute.entity.Booking;
import com.bookmyroute.entity.Route;
import com.bookmyroute.entity.Schedule;
import com.bookmyroute.repository.BookingRepository;
import com.bookmyroute.repository.RouteRepository;
import com.bookmyroute.repository.ScheduleRepository;
import com.bookmyroute.repository.UserRepository;
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
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class ChatbotServiceImpl implements ChatbotService {

    private static final DateTimeFormatter DISPLAY_TIME = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    private final RouteRepository routeRepository;
    private final ScheduleRepository scheduleRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    public ChatbotServiceImpl(RouteRepository routeRepository,
                              ScheduleRepository scheduleRepository,
                              BookingRepository bookingRepository,
                              UserRepository userRepository,
                              RestClient.Builder restClientBuilder,
                              @Value("${openai.api.key:}") String apiKey,
                              @Value("${openai.model:gpt-4o-mini}") String model) {
        this.routeRepository = routeRepository;
        this.scheduleRepository = scheduleRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.restClient = restClientBuilder.baseUrl("https://api.openai.com/v1").build();
        this.apiKey = apiKey;
        this.model = model;
    }

    @Override
    @Transactional(readOnly = true)
    public ChatbotResponse ask(ChatbotRequest request, String userEmail) {
        String cleanMessage = request.getMessage().trim();
        List<ChatbotRequest.HistoryMessage> history = request.getHistory() == null
                ? List.of()
                : request.getHistory();
        String context = buildBookingContext(userEmail);
        List<String> suggestions = buildSuggestions(cleanMessage);

        if (!StringUtils.hasText(apiKey)) {
            return ChatbotResponse.of(buildLocalFallback(cleanMessage, context, userEmail), "LOCAL_FALLBACK", suggestions);
        }

        try {
            JsonNode response = restClient.post()
                    .uri("/responses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(headers -> headers.setBearerAuth(apiKey))
                    .body(buildOpenAiRequest(cleanMessage, history, context, userEmail))
                    .retrieve()
                    .body(JsonNode.class);

            String reply = extractOutputText(response);
            if (!StringUtils.hasText(reply)) {
                reply = "I could not generate a clear answer right now. Please try again with your route, date, or booking question.";
            }
            return ChatbotResponse.of(reply, "OPENAI", suggestions);
        } catch (RestClientException ex) {
            return ChatbotResponse.of(buildLocalFallback(cleanMessage, context, userEmail), "LOCAL_FALLBACK", suggestions);
        }
    }

    private Map<String, Object> buildOpenAiRequest(String message,
                                                   List<ChatbotRequest.HistoryMessage> history,
                                                   String context,
                                                   String userEmail) {
        String instructions = """
                You are BookMyRoute Assistant, a concise support chatbot for a bus ticket booking platform in India.
                Help users with route discovery, schedules, fares, seat selection, booking steps, payment status, cancellation rules, ticket PDF downloads, and account questions.
                Use the provided live BookMyRoute context when answering availability questions.
                If the requested live detail is not in the context, say what information is missing and suggest using route search.
                Never invent booking records, prices, refunds, seat counts, or schedules.
                If the user asks to make, cancel, or modify a booking, explain the exact app steps instead of claiming you performed the action.
                Keep answers friendly, practical, and under 130 words.
                For unrelated questions, answer briefly if harmless, then connect the answer back to BookMyRoute when useful.
                """;

        String inputText = "User email: " + (StringUtils.hasText(userEmail) ? userEmail : "guest or unknown")
                + "\n\nRecent conversation:\n" + formatHistory(history)
                + "\n\nLive BookMyRoute context:\n" + context
                + "\n\nUser question: " + message;

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

    private String buildBookingContext(String userEmail) {
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
        lines.add("Ticket PDF: after confirmation, users can click Ticket PDF on the success screen or Download ticket in My bookings.");
        lines.add("Cancellation: users can cancel eligible CONFIRMED or PENDING bookings from My bookings. Completed bookings cannot be cancelled.");
        lines.add("Admin login: admins use the admin login form with an ADMIN account.");

        if (StringUtils.hasText(userEmail)) {
            List<Booking> bookings = userRepository.findByEmail(userEmail)
                    .map(user -> bookingRepository.findAllByUserId(user.getId()))
                    .orElse(List.of());
            if (bookings.isEmpty()) {
                lines.add("This signed-in user has no bookings yet.");
            } else {
                lines.add("Signed-in user's recent bookings:");
                bookings.stream()
                        .sorted((left, right) -> Comparator.nullsLast(LocalDateTime::compareTo)
                                .compare(right.getBookedAt(), left.getBookedAt()))
                        .limit(5)
                        .forEach(booking -> lines.add(String.format(
                                "- %s: %s to %s on %s, status %s, payment %s, total %s",
                                booking.getBookingRef(),
                                booking.getSchedule().getRoute().getOrigin(),
                                booking.getSchedule().getRoute().getDestination(),
                                DISPLAY_TIME.format(booking.getSchedule().getDepartureTime()),
                                booking.getStatus(),
                                booking.getPayment() == null ? "UNKNOWN" : booking.getPayment().getStatus(),
                                booking.getTotalAmount()
                        )));
            }
        }

        return String.join("\n", lines);
    }

    private String buildLocalFallback(String message, String context, String userEmail) {
        String lowerMessage = message.toLowerCase();

        if (lowerMessage.contains("route") || lowerMessage.contains("available") || lowerMessage.contains("schedule")
                || lowerMessage.contains("bus") || lowerMessage.contains("fare") || lowerMessage.contains("seat")) {
            return "I can help with route, schedule, fare, and seat questions. AI is not configured yet, but here is the latest booking context I can see:\n\n" + context;
        }

        if (lowerMessage.contains("complaint") || lowerMessage.contains("help desk") || lowerMessage.contains("support")
                || lowerMessage.contains("issue") || lowerMessage.contains("problem")) {
            return "For any issue, open Help desk from the navbar, choose the closest issue type, add your booking reference if you have one, and submit it. The admin team can reply by email to the address you provide.";
        }

        if (lowerMessage.contains("login") || lowerMessage.contains("sign in") || lowerMessage.contains("password")
                || lowerMessage.contains("account")) {
            return "Use Login for an existing account or Register for a new one. If you are inactive for a while, BookMyRoute signs you out automatically for safety. Admins should use the Admin mode on the login form.";
        }

        if (lowerMessage.contains("pdf") || lowerMessage.contains("download")) {
            return "You can download a booking confirmation PDF right after payment using Ticket PDF, or later from My bookings using Download ticket.";
        }

        if (lowerMessage.contains("cancel") || lowerMessage.contains("refund") || lowerMessage.contains("deducted")) {
            return "Open My bookings, find the trip, and choose Cancel booking. Eligible confirmed or pending bookings can be cancelled; completed bookings cannot be cancelled.";
        }

        if (lowerMessage.contains("admin")) {
            return "Admins can sign in from the admin login page with an ADMIN account. If login fails, confirm the backend is running and the admin account exists.";
        }

        if (lowerMessage.contains("book") || lowerMessage.contains("payment") || lowerMessage.contains("ticket")) {
            return "To book a ticket, search by origin, destination, travel date, and number of seats. Then choose a schedule, select seats, complete payment, and save the booking confirmation.";
        }

        if (StringUtils.hasText(userEmail) && (lowerMessage.contains("my booking") || lowerMessage.contains("booking ref"))) {
            return "You are signed in as " + userEmail + ". Open My bookings to see your booking references, status, cancellation option, and ticket PDF download.";
        }

        return "I can help with routes, bus timings, fares, seats, booking steps, payments, cancellations, refunds, ticket PDFs, login, and help desk complaints. Tell me what you are trying to do and include your route, date, or booking reference if you have one.";
    }

    private String extractOutputText(JsonNode response) {
        if (response == null) {
            return "";
        }

        if (response.hasNonNull("output_text")) {
            return response.path("output_text").asText().trim();
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

    private String formatHistory(List<ChatbotRequest.HistoryMessage> history) {
        if (history.isEmpty()) {
            return "No recent messages.";
        }

        return history.stream()
                .filter(item -> StringUtils.hasText(item.getRole()) && StringUtils.hasText(item.getText()))
                .limit(12)
                .map(item -> sanitizeHistoryRole(item.getRole()) + ": " + item.getText().trim())
                .reduce((left, right) -> left + "\n" + right)
                .orElse("No recent messages.");
    }

    private String sanitizeHistoryRole(String role) {
        String normalized = role.trim().toLowerCase();
        if ("assistant".equals(normalized) || "user".equals(normalized)) {
            return normalized;
        }
        return "user";
    }

    private List<String> buildSuggestions(String message) {
        String lowerMessage = message.toLowerCase();

        if (lowerMessage.contains("cancel") || lowerMessage.contains("refund")) {
            return List.of("Show my bookings", "Raise complaint", "Download ticket PDF");
        }
        if (lowerMessage.contains("pdf") || lowerMessage.contains("ticket")) {
            return List.of("Where is My bookings?", "Raise complaint", "How do I cancel?");
        }
        if (lowerMessage.contains("complaint") || lowerMessage.contains("support") || lowerMessage.contains("issue")) {
            return List.of("Raise complaint", "Contact support", "Payment deducted");
        }
        if (lowerMessage.contains("route") || lowerMessage.contains("bus") || lowerMessage.contains("seat")) {
            return List.of("Available buses today", "How do I book?", "Fare details");
        }
        return List.of("Available buses today", "Download ticket PDF", "How do I cancel?");
    }
}
