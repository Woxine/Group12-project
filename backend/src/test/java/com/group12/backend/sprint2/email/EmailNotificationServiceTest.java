package com.group12.backend.sprint2.email;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.group12.backend.dto.BookingConfirmationEmailPayload;
import com.group12.backend.service.impl.EmailNotificationServiceImpl;

/**
 * ID7 邮件发送：断言实现完成后可正常完成发送调用（内部可降级日志）；骨架阶段会失败。
 */
@DisplayName("ID7 EmailNotificationService")
class EmailNotificationServiceTest {

    private EmailNotificationServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new EmailNotificationServiceImpl();
    }

    @Test
    @DisplayName("sendBookingConfirmation：不抛出未实现异常")
    void sendBookingConfirmation_completesWithoutThrowing() {
        BookingConfirmationEmailPayload payload = new BookingConfirmationEmailPayload();
        payload.setEmail("guest@example.com");
        payload.setBookingId("b1");
        assertThatCode(() -> service.sendBookingConfirmation(payload)).doesNotThrowAnyException();
    }
}
