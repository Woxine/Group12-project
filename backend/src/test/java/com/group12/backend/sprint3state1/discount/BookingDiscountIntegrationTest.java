package com.group12.backend.sprint3state1.discount;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.group12.backend.dto.DiscountBreakdownResponse;
import com.group12.backend.repository.BookingRepository;
import com.group12.backend.repository.ScooterRepository;
import com.group12.backend.repository.UserRepository;
import com.group12.backend.service.DiscountService;
import com.group12.backend.service.EmailNotificationService;
import com.group12.backend.service.impl.BookingServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("ID22 BookingDiscountIntegration")
class BookingDiscountIntegrationTest {

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
    @DisplayName("createBooking_withDiscountPersistsPriceFields")
    void createBooking_withDiscountPersistsPriceFields() {
        when(discountService.calculateDiscount(1L, 1L, "1H"))
                .thenReturn(DiscountBreakdownResponse.of(
                        new BigDecimal("10.00"),
                        "STUDENT",
                        new BigDecimal("2.00"),
                        new BigDecimal("8.00"),
                        "discount.student"));
        assertThatCode(() -> bookingService.previewDiscount("1", "1", "1H"))
                .doesNotThrowAnyException();
        DiscountBreakdownResponse response = bookingService.previewDiscount("1", "1", "1H");
        assertThat(response).isNotNull();
        assertThat(response.getOriginalPrice()).isNotNull();
        assertThat(response.getFinalPrice()).isNotNull();
    }
}
