package com.group12.backend.sprint3state1.discount;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.group12.backend.config.DiscountProperties;
import com.group12.backend.dto.DiscountBreakdownResponse;
import com.group12.backend.entity.DiscountVerificationSubmission;
import com.group12.backend.entity.Scooter;
import com.group12.backend.entity.User;
import com.group12.backend.repository.BookingRepository;
import com.group12.backend.repository.DiscountVerificationSubmissionRepository;
import com.group12.backend.repository.ScooterRepository;
import com.group12.backend.repository.UserRepository;
import com.group12.backend.service.DiscountVerificationConstants;
import com.group12.backend.service.impl.DiscountServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("ID22 DiscountService")
class DiscountServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ScooterRepository scooterRepository;
    @Mock
    private DiscountVerificationSubmissionRepository discountVerificationSubmissionRepository;

    @InjectMocks
    private DiscountServiceImpl discountService;

    @Test
    @DisplayName("calculateDiscount_studentApplied")
    void calculateDiscount_studentApplied() {
        DiscountProperties properties = new DiscountProperties();
        discountService.setDiscountProperties(properties);
        discountService.setClock(Clock.fixed(Instant.parse("2026-04-14T10:00:00Z"), ZoneId.of("UTC")));

        User user = new User();
        user.setId(1L);
        user.setAge(20);
        when(bookingRepository.sumCompletedDurationHours(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(2.0);
        DiscountVerificationSubmission approved = new DiscountVerificationSubmission();
        approved.setType(DiscountVerificationConstants.TYPE_STUDENT);
        approved.setStatus(DiscountVerificationConstants.STATUS_APPROVED);
        when(discountVerificationSubmissionRepository.findTopByUser_IdAndTypeAndStatusOrderByVersionDesc(
                1L, DiscountVerificationConstants.TYPE_STUDENT, DiscountVerificationConstants.STATUS_APPROVED
        )).thenReturn(Optional.of(approved));

        Scooter scooter = new Scooter();
        scooter.setId(1L);
        scooter.setHourRate(new BigDecimal("10.00"));
        when(scooterRepository.findById(1L)).thenReturn(Optional.of(scooter));

        DiscountBreakdownResponse response = discountService.calculateDiscount(1L, 1L, "1H");
        assertThat(response).isNotNull();
        assertThat(response.getDiscountType()).isEqualTo("STUDENT");
        assertThat(response.getOriginalPrice()).isEqualByComparingTo("10.00");
        assertThat(response.getFinalPrice()).isEqualByComparingTo("8.00");
        assertThat(response.getFinalPrice()).isLessThan(response.getOriginalPrice());
    }

    @Test
    @DisplayName("calculateDiscount_priorityRuleStable")
    void calculateDiscount_priorityRuleStable() {
        DiscountProperties properties = new DiscountProperties();
        discountService.setDiscountProperties(properties);
        discountService.setClock(Clock.fixed(Instant.parse("2026-04-14T10:00:00Z"), ZoneId.of("UTC")));

        User user = new User();
        user.setId(999L);
        user.setAge(70);
        when(bookingRepository.sumCompletedDurationHours(eq(999L), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(9.0);

        String type = discountService.resolveDiscountType(999L);
        assertThat(type).isEqualTo("FREQUENT");
    }

    @Test
    @DisplayName("applyDiscount_reducesPrice")
    void applyDiscount_reducesPrice() {
        DiscountProperties properties = new DiscountProperties();
        discountService.setDiscountProperties(properties);
        BigDecimal finalPrice = discountService.applyDiscount(new BigDecimal("100.00"), "STUDENT");
        assertThat(finalPrice).isEqualByComparingTo("80.00");
    }

    @Test
    @DisplayName("calculateDiscount_studentPending_notApplied")
    void calculateDiscount_studentPending_notApplied() {
        DiscountProperties properties = new DiscountProperties();
        discountService.setDiscountProperties(properties);
        discountService.setClock(Clock.fixed(Instant.parse("2026-04-14T10:00:00Z"), ZoneId.of("UTC")));

        User user = new User();
        user.setId(2L);
        user.setAge(20);
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(bookingRepository.sumCompletedDurationHours(eq(2L), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(0.0);
        when(discountVerificationSubmissionRepository.findTopByUser_IdAndTypeAndStatusOrderByVersionDesc(
                2L, DiscountVerificationConstants.TYPE_STUDENT, DiscountVerificationConstants.STATUS_APPROVED
        )).thenReturn(Optional.empty());

        Scooter scooter = new Scooter();
        scooter.setId(1L);
        scooter.setHourRate(new BigDecimal("10.00"));
        when(scooterRepository.findById(1L)).thenReturn(Optional.of(scooter));

        DiscountBreakdownResponse response = discountService.calculateDiscount(2L, 1L, "1H");
        assertThat(response.getDiscountType()).isEqualTo("NONE");
        assertThat(response.getFinalPrice()).isEqualByComparingTo("10.00");
    }
}
