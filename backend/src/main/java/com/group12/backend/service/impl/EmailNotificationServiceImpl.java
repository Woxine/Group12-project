package com.group12.backend.service.impl;

import org.springframework.stereotype.Service;

import com.group12.backend.dto.BookingConfirmationEmailPayload;
import com.group12.backend.service.EmailNotificationService;

/**
 * TODO(ID7): 邮件通知实现骨架（仅 TODO，不含业务实现）。
 */
@Service
public class EmailNotificationServiceImpl implements EmailNotificationService {
    @Override
    public void sendBookingConfirmation(BookingConfirmationEmailPayload payload) {
        // TODO: 构建邮件模板并发送
        // TODO: 发送失败降级处理，不影响主业务
        throw new UnsupportedOperationException("TODO: implement sendBookingConfirmation");
    }
}
