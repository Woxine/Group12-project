package com.group12.backend.sprint2.bookingextension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.group12.backend.dto.ExtendBookingRequest;
import com.group12.backend.entity.Booking;
import com.group12.backend.entity.Scooter;
import com.group12.backend.entity.User;
import com.group12.backend.repository.BookingRepository;
import com.group12.backend.service.impl.BookingExtensionServiceImpl;

/**
 * ID10/11 预订延长：断言实现完成后的契约；骨架阶段失败。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ID10/11 BookingExtensionService")
class BookingExtensionServiceTest {

    @InjectMocks
    private BookingExtensionServiceImpl service;

    @Mock
    private BookingRepository bookingRepository;

    @Test
    @DisplayName("extendBooking：成功时返回非空结果且不抛异常")
    void extendBooking_returnsNonNull_whenValid() {
        User user = new User();
        user.setId(99L);

        Scooter scooter = new Scooter();
        scooter.setId(10L);
        scooter.setHourRate(new BigDecimal("10.0"));

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setUser(user);
        booking.setScooter(scooter);
        booking.setStatus("CONFIRMED");
        booking.setStartTime(LocalDateTime.now().minusHours(1));
        booking.setEndTime(LocalDateTime.now().plusHours(1));
        booking.setDurationHours(2.0);
        booking.setTotalPrice(new BigDecimal("20.0"));

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ExtendBookingRequest req = new ExtendBookingRequest();
        req.setDuration("1H");

        assertThatCode(() -> service.extendBooking("1", req, 99L)).doesNotThrowAnyException();
        Object result = service.extendBooking("1", req, 99L);
        assertThat(result).isNotNull();
    }
}
