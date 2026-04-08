package com.group12.backend.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.group12.backend.dto.BookingConfirmationEmailPayload;
import com.group12.backend.service.EmailNotificationService;

/**
 * TODO(ID7): 邮件通知实现骨架（仅 TODO，不含业务实现）。
 */
@Service
public class EmailNotificationServiceImpl implements EmailNotificationService {
    private static final Logger log = LoggerFactory.getLogger(EmailNotificationServiceImpl.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Override
    @Async("mailExecutor")
    public void sendBookingConfirmation(BookingConfirmationEmailPayload payload) {
        if (payload == null) {
            return;
        }

        String to = trimToEmpty(payload.getEmail());
        if (to.isEmpty()) {
            log.info("Skip booking confirmation email: recipient is empty.");
            return;
        }

        try {
            MailTemplate template = buildBookingTemplate(payload);
            sendMail(to, template.subject(), template.body());
        } catch (Exception ex) {
            // 邮件链路故障不应影响下单主流程
            log.warn("Booking confirmation email degraded for bookingId={}", payload.getBookingId(), ex);
        }
    }

    MailTemplate buildBookingTemplate(BookingConfirmationEmailPayload payload) {
        String bookingId = defaultText(payload.getBookingId(), "-");
        String scooterId = defaultText(payload.getScooterId(), "-");
        String duration = defaultText(payload.getDuration(), "-");
        String totalPrice = defaultText(payload.getTotalPrice(), "-");

        String subject = "Booking Confirmation #" + bookingId;
        String body = "Your booking has been confirmed.\n"
                + "Booking ID: " + bookingId + "\n"
                + "Scooter ID: " + scooterId + "\n"
                + "Duration: " + duration + "\n"
                + "Total Price: " + totalPrice + "\n";
        return new MailTemplate(subject, body);
    }

    void sendMail(String to, String subject, String body) {
        try {
            if (mailSender == null) {
                log.warn("Mail sender is not configured, skip sending booking confirmation email.");
                return;
            }

            SimpleMailMessage message = new SimpleMailMessage();
            if (!trimToEmpty(fromEmail).isEmpty()) {
                message.setFrom(fromEmail);
            }
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Booking confirmation email sent to={}", to);
        } catch (Exception ex) {
            // 防止任何邮件发送异常影响主业务链路
            log.warn("Email send failed for recipient={}", to, ex);
        }
    }

    private String defaultText(String text, String fallback) {
        String trimmed = trimToEmpty(text);
        return trimmed.isEmpty() ? fallback : trimmed;
    }

    private String trimToEmpty(String text) {
        return text == null ? "" : text.trim();
    }

    record MailTemplate(String subject, String body) {
    }
}
