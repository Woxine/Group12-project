package com.group12.backend.sprint3state1.booking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.group12.backend.config.BillingProperties;
import com.group12.backend.dto.BookingResponse;
import com.group12.backend.dto.CreateBookingRequest;
import com.group12.backend.dto.PayBookingRequest;
import com.group12.backend.entity.Booking;
import com.group12.backend.entity.Payment;
import com.group12.backend.entity.Scooter;
import com.group12.backend.entity.User;
import com.group12.backend.exception.BusinessException;
import com.group12.backend.repository.BookingRepository;
import com.group12.backend.repository.PaymentCardRepository;
import com.group12.backend.repository.PaymentRepository;
import com.group12.backend.repository.ScooterRepository;
import com.group12.backend.repository.UserRepository;
import com.group12.backend.service.BillingRule;
import com.group12.backend.service.BillingService;
import com.group12.backend.service.BookingCompletionService;
import com.group12.backend.service.EmailNotificationService;
import com.group12.backend.service.impl.BookingServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("Booking payment gate")
class BookingPaymentGateTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ScooterRepository scooterRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PaymentCardRepository paymentCardRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private BillingService billingService;
    @Mock
    private BillingProperties billingProperties;
    @Mock
    private EmailNotificationService emailNotificationService;

    @InjectMocks
    private BookingServiceImpl bookingService;
    @InjectMocks
    private BookingCompletionService bookingCompletionService;

    @Test
    @DisplayName("createBooking reserves scooter and returns pending payment")
    void createBooking_reservesScooterAndReturnsPendingPayment() {
        User user = buildUser(1L);
        Scooter scooter = buildAvailableScooter(2L);
        Booking[] persisted = new Booking[1];

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(paymentCardRepository.existsByUser_Id(1L)).thenReturn(true);
        when(bookingRepository.findByUser_IdAndStatus(1L, "CONFIRMED")).thenReturn(List.of());
        when(bookingRepository.findByUser_IdAndStatus(1L, "PENDING_PAYMENT")).thenReturn(List.of());
        when(scooterRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(scooter));
        when(bookingRepository.findOverlappingBookings(eq(2L), any(), any())).thenReturn(List.of());
        when(billingService.getCurrentRule()).thenReturn(defaultRule());
        when(billingProperties.getPendingPaymentLockMinutes()).thenReturn(5);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            booking.setId(88L);
            persisted[0] = booking;
            return booking;
        });
        when(scooterRepository.save(any(Scooter.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreateBookingRequest request = new CreateBookingRequest();
        request.setUser_id("1");
        request.setScooter_id("2");
        request.setDuration("1H");

        BookingResponse response = (BookingResponse) bookingService.createBooking(request);

        assertThat(response.getStatus()).isEqualTo("PENDING_PAYMENT");
        assertThat(response.getPaymentDeadline()).isNotBlank();
        assertThat(scooter.getStatus()).isEqualTo("RESERVED");
        assertThat(persisted[0]).isNotNull();
        assertThat(persisted[0].getPaymentDeadline()).isNotNull();
        assertThat(persisted[0].getPaymentDeadline()).isAfter(persisted[0].getStartTime());
    }

    @Test
    @DisplayName("payBooking confirms booking and writes payment record")
    void payBooking_confirmsBookingAndWritesPaymentRecord() {
        User user = buildUser(3L);
        Scooter scooter = buildReservedScooter(5L);
        Booking booking = buildPendingBooking(101L, user, scooter, LocalDateTime.now().plusMinutes(5));

        when(bookingRepository.findByIdForUpdate(101L)).thenReturn(Optional.of(booking));
        when(paymentRepository.findByBookingId(101L)).thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(scooterRepository.save(any(Scooter.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PayBookingRequest request = new PayBookingRequest();
        request.setPaymentMethod("WeChat Pay");

        BookingResponse response = (BookingResponse) bookingService.payBooking("101", 3L, request);

        assertThat(response.getStatus()).isEqualTo("CONFIRMED");
        assertThat(booking.getPaymentDeadline()).isNull();
        assertThat(scooter.getStatus()).isEqualTo("RENTED");

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());
        assertThat(paymentCaptor.getValue().getPaymentMethod()).isEqualTo("WECHAT_PAY");
        assertThat(paymentCaptor.getValue().getAmount()).isEqualByComparingTo("12.00");
    }

    @Test
    @DisplayName("expired pending booking is cancelled and scooter released")
    void expirePendingPaymentBooking_cancelsBookingAndReleasesScooter() {
        Scooter scooter = buildReservedScooter(8L);
        Booking booking = buildPendingBooking(202L, buildUser(9L), scooter, LocalDateTime.now().minusMinutes(1));

        when(bookingRepository.findByIdForUpdate(202L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(scooterRepository.save(any(Scooter.class))).thenAnswer(invocation -> invocation.getArgument(0));

        bookingCompletionService.expirePendingPaymentBooking(202L);

        assertThat(booking.getStatus()).isEqualTo("CANCELLED");
        assertThat(booking.getPaymentDeadline()).isNull();
        assertThat(scooter.getStatus()).isEqualTo("AVAILABLE");
    }

    @Test
    @DisplayName("payBooking rejects requests after deadline")
    void payBooking_rejectsExpiredRequest() {
        User user = buildUser(4L);
        Scooter scooter = buildReservedScooter(6L);
        Booking booking = buildPendingBooking(303L, user, scooter, LocalDateTime.now().minusSeconds(5));

        when(bookingRepository.findByIdForUpdate(303L)).thenReturn(Optional.of(booking));

        PayBookingRequest request = new PayBookingRequest();
        request.setPaymentMethod("Bank Card");

        assertThatThrownBy(() -> bookingService.payBooking("303", 4L, request))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getStatus())
                .isEqualTo(HttpStatus.CONFLICT);
    }

    private User buildUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setEmail("user" + id + "@example.com");
        user.setName("User " + id);
        user.setPassword("pwd");
        user.setRole("CUSTOMER");
        return user;
    }

    private Scooter buildAvailableScooter(Long id) {
        Scooter scooter = new Scooter();
        scooter.setId(id);
        scooter.setVisible(true);
        scooter.setStatus("AVAILABLE");
        scooter.setHourRate(new BigDecimal("12.00"));
        return scooter;
    }

    private Scooter buildReservedScooter(Long id) {
        Scooter scooter = buildAvailableScooter(id);
        scooter.setStatus("RESERVED");
        return scooter;
    }

    private Booking buildPendingBooking(Long bookingId, User user, Scooter scooter, LocalDateTime paymentDeadline) {
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setUser(user);
        booking.setScooter(scooter);
        booking.setStatus("PENDING_PAYMENT");
        booking.setStartTime(LocalDateTime.now());
        booking.setEndTime(LocalDateTime.now().plusHours(1));
        booking.setDurationHours(1.0);
        booking.setTotalPrice(new BigDecimal("12.00"));
        booking.setOriginalPrice(new BigDecimal("12.00"));
        booking.setDiscountAmount(BigDecimal.ZERO);
        booking.setDiscountMultiplier(BigDecimal.ONE);
        booking.setDiscountType("NONE");
        booking.setPaymentDeadline(paymentDeadline);
        return booking;
    }

    private BillingRule defaultRule() {
        return new BillingRule(
                new BigDecimal("24"),
                new BigDecimal("72"),
                new BigDecimal("0.85"),
                new BigDecimal("0.75"),
                new BigDecimal("0.80"),
                new BigDecimal("0.80"),
                new BigDecimal("0.80"),
                LocalDateTime.now());
    }
}
