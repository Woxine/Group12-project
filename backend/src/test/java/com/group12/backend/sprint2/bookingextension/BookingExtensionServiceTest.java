package com.group12.backend.sprint2.bookingextension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.group12.backend.dto.ExtendBookingRequest;
import com.group12.backend.service.impl.BookingExtensionServiceImpl;

/**
 * ID10/11 预订延长：断言实现完成后的契约；骨架阶段失败。
 */
@DisplayName("ID10/11 BookingExtensionService")
class BookingExtensionServiceTest {

    private BookingExtensionServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new BookingExtensionServiceImpl();
    }

    @Test
    @DisplayName("extendBooking：成功时返回非空结果且不抛异常")
    void extendBooking_returnsNonNull_whenValid() {
        ExtendBookingRequest req = new ExtendBookingRequest();
        req.setDuration("1H");
        assertThatCode(() -> service.extendBooking("b1", req, 99L)).doesNotThrowAnyException();
        Object result = service.extendBooking("b1", req, 99L);
        assertThat(result).isNotNull();
    }
}
