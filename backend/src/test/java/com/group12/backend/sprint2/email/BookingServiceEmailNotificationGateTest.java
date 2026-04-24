package com.group12.backend.sprint2.email;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
import com.group12.backend.dto.PayBookingRequest;
import com.group12.backend.config.BillingProperties;
import com.group12.backend.entity.Booking;
import com.group12.backend.entity.Payment;
import com.group12.backend.entity.Scooter;
import com.group12.backend.entity.User;
import com.group12.backend.repository.BookingRepository;
import com.group12.backend.repository.PaymentRepository;
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
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private BillingProperties billingProperties;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    @DisplayName("支付成功后须触发确认邮件")
    void payBooking_mustTriggerConfirmationEmail() {
        User user = new User();
        user.setId(1L);
        user.setEmail("guest@example.com");

        Scooter scooter = new Scooter();
        scooter.setId(2L);
        scooter.setStatus("AVAILABLE");
        scooter.setHourRate(new BigDecimal("20.00"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findByUser_IdAndStatus(1L, "CONFIRMED")).thenReturn(Collections.emptyList());
        when(bookingRepository.findByUser_IdAndStatus(1L, "PENDING_PAYMENT")).thenReturn(Collections.emptyList());
        when(scooterRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(scooter));
        when(bookingRepository.findOverlappingBookings(eq(2L), any(), any())).thenReturn(Collections.emptyList());
        when(billingProperties.getPendingPaymentLockMinutes()).thenReturn(5);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking saved = invocation.getArgument(0);
            if (saved.getId() == null) {
                saved.setId(99L);
            }
            return saved;
        });
        when(paymentRepository.findByBookingId(99L)).thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(scooterRepository.save(any(Scooter.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreateBookingRequest request = new CreateBookingRequest();
        request.setUser_id("1");
        request.setScooter_id("2");
        request.setDuration("1H");

        Object response = bookingService.createBooking(request);

        assertThat(response).isInstanceOf(BookingResponse.class);
        verifyNoInteractions(emailNotificationService);

        Booking pendingBooking = new Booking();
        pendingBooking.setId(99L);
        pendingBooking.setUser(user);
        pendingBooking.setScooter(scooter);
        pendingBooking.setStatus("PENDING_PAYMENT");
        pendingBooking.setStartTime(java.time.LocalDateTime.now());
        pendingBooking.setEndTime(java.time.LocalDateTime.now().plusHours(1));
        pendingBooking.setDurationHours(1.0);
        pendingBooking.setTotalPrice(new BigDecimal("20.00"));
        pendingBooking.setOriginalPrice(new BigDecimal("20.00"));
        pendingBooking.setDiscountAmount(BigDecimal.ZERO);
        pendingBooking.setDiscountMultiplier(BigDecimal.ONE);
        pendingBooking.setDiscountType("NONE");
        pendingBooking.setPaymentDeadline(java.time.LocalDateTime.now().plusMinutes(5));
        when(bookingRepository.findByIdForUpdate(99L)).thenReturn(Optional.of(pendingBooking));

        PayBookingRequest payRequest = new PayBookingRequest();
        payRequest.setPaymentMethod("Bank Card");
        bookingService.payBooking("99", 1L, payRequest);

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
