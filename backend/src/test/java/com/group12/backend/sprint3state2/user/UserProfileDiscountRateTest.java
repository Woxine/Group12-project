package com.group12.backend.sprint3state2.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.group12.backend.entity.User;
import com.group12.backend.repository.BookingRepository;
import com.group12.backend.repository.UserRepository;
import com.group12.backend.service.BillingRule;
import com.group12.backend.service.BillingService;
import com.group12.backend.service.DiscountService;
import com.group12.backend.service.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("Sprint3 UserProfile discount rates")
class UserProfileDiscountRateTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BillingService billingService;
    @Mock
    private DiscountService discountService;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("profile includes discount rates when BillingService available")
    void profileIncludesDiscountRates() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setRole("CUSTOMER");
        user.setAge(22);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(discountService.resolveDiscountType(1L)).thenReturn("STUDENT");
        when(discountService.hasFrequentDiscount(1L)).thenReturn(false);
        when(discountService.hasStudentDiscount(1L)).thenReturn(true);
        when(discountService.hasSeniorDiscount(1L)).thenReturn(false);

        BillingRule rule = new BillingRule(
                new BigDecimal("24"),
                new BigDecimal("72"),
                new BigDecimal("1.0"),
                new BigDecimal("1.0"),
                new BigDecimal("0.80"),
                new BigDecimal("0.85"),
                new BigDecimal("0.90"),
                LocalDateTime.now());
        when(billingService.getCurrentRule()).thenReturn(rule);

        Map<String, Object> profile = (Map<String, Object>) userService.getUserProfile("1");

        assertThat(profile.get("studentDiscountRate")).isEqualTo(new BigDecimal("0.80"));
        assertThat(profile.get("seniorDiscountRate")).isEqualTo(new BigDecimal("0.85"));
        assertThat(profile.get("frequentDiscountRate")).isEqualTo(new BigDecimal("0.90"));
    }

    @Test
    @DisplayName("profile returns null rates when BillingService returns null rule")
    void profileReturnsNullRatesWhenRuleNull() {
        User user = new User();
        user.setId(2L);
        user.setEmail("test2@example.com");
        user.setName("Test User 2");
        user.setRole("CUSTOMER");
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        when(discountService.resolveDiscountType(2L)).thenReturn("NONE");
        when(discountService.hasFrequentDiscount(2L)).thenReturn(false);
        when(discountService.hasStudentDiscount(2L)).thenReturn(false);
        when(discountService.hasSeniorDiscount(2L)).thenReturn(false);

        when(billingService.getCurrentRule()).thenReturn(null);

        Map<String, Object> profile = (Map<String, Object>) userService.getUserProfile("2");

        assertThat(profile.get("studentDiscountRate")).isNull();
        assertThat(profile.get("seniorDiscountRate")).isNull();
        assertThat(profile.get("frequentDiscountRate")).isNull();
    }

    @Test
    @DisplayName("existing discount flags unchanged after adding rates")
    void existingDiscountFlagsUnchanged() {
        User user = new User();
        user.setId(3L);
        user.setEmail("test3@example.com");
        user.setName("Test User 3");
        user.setRole("CUSTOMER");
        user.setAge(65);
        when(userRepository.findById(3L)).thenReturn(Optional.of(user));

        when(discountService.resolveDiscountType(3L)).thenReturn("SENIOR");
        when(discountService.hasFrequentDiscount(3L)).thenReturn(false);
        when(discountService.hasStudentDiscount(3L)).thenReturn(false);
        when(discountService.hasSeniorDiscount(3L)).thenReturn(true);

        BillingRule rule = new BillingRule(
                new BigDecimal("24"),
                new BigDecimal("72"),
                new BigDecimal("1.0"),
                new BigDecimal("1.0"),
                new BigDecimal("0.80"),
                new BigDecimal("0.75"),
                new BigDecimal("0.90"),
                LocalDateTime.now());
        when(billingService.getCurrentRule()).thenReturn(rule);

        Map<String, Object> profile = (Map<String, Object>) userService.getUserProfile("3");

        assertThat(profile.get("discountType")).isEqualTo("SENIOR");
        assertThat(profile.get("hasFrequentDiscount")).isEqualTo(false);
        assertThat(profile.get("hasStudentDiscount")).isEqualTo(false);
        assertThat(profile.get("hasSeniorDiscount")).isEqualTo(true);
    }
}
