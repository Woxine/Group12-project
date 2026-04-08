package com.group12.backend.sprint2.email;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.group12.backend.dto.BookingConfirmationEmailPayload;
import com.group12.backend.dto.BookingResponse;
import com.group12.backend.dto.CreateBookingRequest;
import com.group12.backend.entity.Booking;
import com.group12.backend.entity.Scooter;
import com.group12.backend.entity.User;
import com.group12.backend.repository.BookingRepository;
import com.group12.backend.repository.ScooterRepository;
import com.group12.backend.repository.UserRepository;
import com.group12.backend.service.EmailNotificationService;
import com.group12.backend.service.impl.BookingServiceImpl;

/**
 * ID7 在下单成功路径中触发确认邮件的集成门禁。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ID7 Booking → email gate")
class BookingServiceEmailNotificationGateTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ScooterRepository scooterRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EmailNotificationService emailNotificationService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    @DisplayName("预订创建成功后须触发确认邮件")
    void createBooking_mustTriggerConfirmationEmail() {
        User user = new User();
        user.setId(1L);
        user.setEmail("guest@example.com");

        Scooter scooter = new Scooter();
        scooter.setId(2L);
        scooter.setStatus("AVAILABLE");
        scooter.setHourRate(new BigDecimal("20.00"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findByUser_IdAndStatus(1L, "CONFIRMED")).thenReturn(Collections.emptyList());
        when(scooterRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(scooter));
        when(bookingRepository.findOverlappingBookings(eq(2L), any(), any())).thenReturn(Collections.emptyList());
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking saved = invocation.getArgument(0);
            saved.setId(99L);
            return saved;
        });

        CreateBookingRequest request = new CreateBookingRequest();
        request.setUser_id("1");
        request.setScooter_id("2");
        request.setDuration("1H");

        Object response = bookingService.createBooking(request);

        assertThat(response).isInstanceOf(BookingResponse.class);

        ArgumentCaptor<BookingConfirmationEmailPayload> payloadCaptor =
                ArgumentCaptor.forClass(BookingConfirmationEmailPayload.class);
        verify(emailNotificationService).sendBookingConfirmation(payloadCaptor.capture());

        BookingConfirmationEmailPayload payload = payloadCaptor.getValue();
        assertThat(payload.getEmail()).isEqualTo("guest@example.com");
        assertThat(payload.getBookingId()).isEqualTo("99");
        assertThat(payload.getScooterId()).isEqualTo("2");
        assertThat(payload.getDuration()).isEqualTo("1H");
        assertThat(new BigDecimal(payload.getTotalPrice())).isEqualByComparingTo("20.00");
    }
}
