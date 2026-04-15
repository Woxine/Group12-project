package com.group12.backend.sprint3state1.guestbooking;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.group12.backend.dto.CreateGuestBookingRequest;
import com.group12.backend.entity.Booking;
import com.group12.backend.entity.Scooter;
import com.group12.backend.entity.User;
import com.group12.backend.repository.BookingRepository;
import com.group12.backend.repository.PaymentCardRepository;
import com.group12.backend.repository.ScooterRepository;
import com.group12.backend.repository.UserRepository;
import com.group12.backend.service.DiscountService;
import com.group12.backend.service.EmailNotificationService;
import com.group12.backend.service.impl.BookingServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("ID9 GuestBookingService")
class GuestBookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private PaymentCardRepository paymentCardRepository;
    @Mock
    private ScooterRepository scooterRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EmailNotificationService emailNotificationService;
    @Mock
    private DiscountService discountService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    @DisplayName("createGuestBooking_success")
    void createGuestBooking_success() {
        CreateGuestBookingRequest request = new CreateGuestBookingRequest();
        request.setSalespersonId("10");
        request.setGuestId("G100");
        request.setGuestName("Guest A");
        request.setGuestContact("guest@example.com");
        request.setScooterId("1");
        request.setDuration("1H");

        User salesperson = new User();
        salesperson.setId(10L);
        salesperson.setRole("STAFF");
        User guest = new User();
        guest.setId(100L);
        guest.setRole("CUSTOMER");
        Scooter scooter = new Scooter();
        scooter.setId(1L);
        scooter.setStatus("AVAILABLE");
        scooter.setVisible(true);
        scooter.setHourRate(BigDecimal.ONE);
        Booking savedBooking = new Booking();
        savedBooking.setId(501L);
        when(userRepository.findById(10L)).thenReturn(Optional.of(salesperson));
        when(userRepository.findById(100L)).thenReturn(Optional.of(guest));
        when(paymentCardRepository.existsByUser_Id(100L)).thenReturn(true);
        when(bookingRepository.findByUser_IdAndStatus(100L, "CONFIRMED")).thenReturn(java.util.List.of());
        when(scooterRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(scooter));
        when(bookingRepository.findOverlappingBookings(any(), any(), any())).thenReturn(java.util.List.of());
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);
        when(scooterRepository.save(any(Scooter.class))).thenReturn(scooter);

        Object response = bookingService.createGuestBooking(request);
        assertThat(response).isNotNull();
    }
}
