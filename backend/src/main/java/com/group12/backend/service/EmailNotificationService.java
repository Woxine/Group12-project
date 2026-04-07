package com.group12.backend.service;

import com.group12.backend.dto.BookingConfirmationEmailPayload;

/**
 * TODO(ID7): 邮件通知服务骨架。
 */
public interface EmailNotificationService {
    void sendBookingConfirmation(BookingConfirmationEmailPayload payload);
}
