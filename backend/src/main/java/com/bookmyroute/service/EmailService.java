package com.bookmyroute.service;

import com.bookmyroute.entity.Booking;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final String fromEmail;
    private final String senderName;

    public EmailService(JavaMailSender mailSender,
                        TemplateEngine templateEngine,
                        @Value("${app.mail.from}") String fromEmail,
                        @Value("${app.mail.sender-name:BookMyRoute}") String senderName) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.fromEmail = fromEmail;
        this.senderName = senderName;
    }

    public void sendBookingConfirmation(Booking booking) throws MessagingException, UnsupportedEncodingException {
        Context context = createBookingContext(booking);
        String html = templateEngine.process("email/booking-confirmation", context);
        sendHtmlEmail(
                booking.getUser().getEmail(),
                "Booking Confirmed - " + booking.getBookingRef(),
                html
        );
    }

    public void sendBookingCancellation(Booking booking) throws MessagingException, UnsupportedEncodingException {
        Context context = createBookingContext(booking);
        String html = templateEngine.process("email/booking-cancellation", context);
        sendHtmlEmail(
                booking.getUser().getEmail(),
                "Booking Cancelled - " + booking.getBookingRef(),
                html
        );
    }

    private Context createBookingContext(Booking booking) {
        Context context = new Context();
        context.setVariable("booking", booking);
        context.setVariable("user", booking.getUser());
        context.setVariable("schedule", booking.getSchedule());
        context.setVariable("route", booking.getSchedule().getRoute());
        context.setVariable("bus", booking.getSchedule().getBus());
        context.setVariable("seats", booking.getBookingSeats());
        return context;
    }

    private void sendHtmlEmail(String to, String subject, String html) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name()
        );

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setFrom(fromEmail, senderName);
        helper.setText(html, true);

        mailSender.send(message);
    }
}
