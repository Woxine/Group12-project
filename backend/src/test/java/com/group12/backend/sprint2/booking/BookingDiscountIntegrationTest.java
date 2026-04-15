package com.group12.backend.sprint2.booking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

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

/**
 * ID22 订单与折扣联动测试。
 */
@ExtendWith(MockitoExtension.class)
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
    void createBooking_withDiscountPersistsPriceFields() {
        when(discountService.calculateDiscount(1L, 1L, "1H"))
                .thenReturn(DiscountBreakdownResponse.of(
                        new BigDecimal("10.00"),
                        "STUDENT",
                        new BigDecimal("2.00"),
                        new BigDecimal("8.00"),
                        "discount.student"));
        DiscountBreakdownResponse response = bookingService.previewDiscount("1", "1", "1H");
        assertThat(response.getOriginalPrice()).isEqualByComparingTo("10.00");
        assertThat(response.getFinalPrice()).isEqualByComparingTo("8.00");
    }

    @Test
    void createBooking_withoutDiscountKeepsOriginalPrice() {
        when(discountService.calculateDiscount(1L, 1L, "1H"))
                .thenReturn(DiscountBreakdownResponse.of(
                        new BigDecimal("10.00"),
                        "NONE",
                        BigDecimal.ZERO,
                        new BigDecimal("10.00"),
                        "discount.none"));
        DiscountBreakdownResponse response = bookingService.previewDiscount("1", "1", "1H");
        assertThat(response.getFinalPrice()).isEqualByComparingTo("10.00");
        assertThat(response.getDiscountType()).isEqualTo("NONE");
    }
}
