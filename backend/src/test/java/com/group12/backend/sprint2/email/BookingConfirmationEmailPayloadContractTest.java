package com.group12.backend.sprint2.email;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.group12.backend.dto.BookingConfirmationEmailPayload;

/**
 * ID7 邮件载荷 DTO 契约（与 {@code *Booking*Email*Test} 脚本过滤匹配）。
 * 模板拼装实现后，可在此补充“必填字段非空”等校验测试。
 */
@DisplayName("ID7 Booking confirmation email payload")
class BookingConfirmationEmailPayloadContractTest {

    @Test
    @DisplayName("可读写邮件模板所需字段")
    void payload_roundTrip() {
        BookingConfirmationEmailPayload p = new BookingConfirmationEmailPayload();
        p.setEmail("u@example.com");
        p.setBookingId("42");
        p.setScooterId("7");
        p.setDuration("1H");
        p.setTotalPrice("19.99");

        assertThat(p.getEmail()).isEqualTo("u@example.com");
        assertThat(p.getBookingId()).isEqualTo("42");
        assertThat(p.getScooterId()).isEqualTo("7");
        assertThat(p.getDuration()).isEqualTo("1H");
        assertThat(p.getTotalPrice()).isEqualTo("19.99");
    }
}
