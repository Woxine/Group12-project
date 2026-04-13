package com.group12.backend.sprint3state1.guestbooking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.group12.backend.dto.CreateGuestBookingRequest;
import com.group12.backend.repository.BookingRepository;
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
        request.setGuestName("Guest A");
        request.setGuestContact("guest@example.com");
        request.setScooterId("1");
        request.setDuration("1H");

        assertThatCode(() -> bookingService.createGuestBooking(request)).doesNotThrowAnyException();
        Object response = bookingService.createGuestBooking(request);
        assertThat(response).isNotNull();
    }
}
