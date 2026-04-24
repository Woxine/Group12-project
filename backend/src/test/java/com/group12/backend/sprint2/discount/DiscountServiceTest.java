package com.group12.backend.sprint2.discount;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

/**
 * ID22 折扣服务核心分支测试。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ID22 Sprint2 DiscountService")
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

    private void prepareCommon(Long userId, boolean isStudent, Integer age, double usedHoursInWeek) {
        DiscountProperties properties = new DiscountProperties();
        discountService.setDiscountProperties(properties);
        discountService.setClock(Clock.fixed(Instant.parse("2026-04-14T10:00:00Z"), ZoneId.of("UTC")));

        User user = new User();
        user.setId(userId);
        user.setIsStudent(isStudent);
        user.setAge(age);
        lenient().when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        lenient().when(bookingRepository.sumCompletedDurationHours(eq(userId), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(usedHoursInWeek);

        Scooter scooter = new Scooter();
        scooter.setId(1L);
        scooter.setHourRate(new BigDecimal("10.00"));
        lenient().when(scooterRepository.findById(1L)).thenReturn(Optional.of(scooter));

        if (isStudent) {
            DiscountVerificationSubmission approved = new DiscountVerificationSubmission();
            approved.setType(DiscountVerificationConstants.TYPE_STUDENT);
            approved.setStatus(DiscountVerificationConstants.STATUS_APPROVED);
            lenient().when(discountVerificationSubmissionRepository.findTopByUser_IdAndTypeAndStatusOrderByVersionDesc(
                    userId, DiscountVerificationConstants.TYPE_STUDENT, DiscountVerificationConstants.STATUS_APPROVED
            )).thenReturn(Optional.of(approved));
        } else {
            lenient().when(discountVerificationSubmissionRepository.findTopByUser_IdAndTypeAndStatusOrderByVersionDesc(
                    userId, DiscountVerificationConstants.TYPE_STUDENT, DiscountVerificationConstants.STATUS_APPROVED
            )).thenReturn(Optional.empty());
        }
        lenient().when(discountVerificationSubmissionRepository.findTopByUser_IdAndTypeAndStatusOrderByVersionDesc(
                userId, DiscountVerificationConstants.TYPE_SENIOR, DiscountVerificationConstants.STATUS_APPROVED
        )).thenReturn(Optional.empty());
    }

    @Test
    @DisplayName("calculateDiscount_studentApplied")
    void calculateDiscount_studentApplied() {
        prepareCommon(1L, true, 22, 2.0);
        DiscountBreakdownResponse response = discountService.calculateDiscount(1L, 1L, "1H");
        assertThat(response.getDiscountType()).isEqualTo("STUDENT");
        assertThat(response.getFinalPrice()).isEqualByComparingTo("8.00");
    }

    @Test
    @DisplayName("calculateDiscount_seniorApplied")
    void calculateDiscount_seniorApplied() {
        prepareCommon(2L, false, 60, 1.0);
        DiscountBreakdownResponse response = discountService.calculateDiscount(2L, 1L, "1H");
        assertThat(response.getDiscountType()).isEqualTo("SENIOR");
    }

    @Test
    @DisplayName("calculateDiscount_frequentApplied")
    void calculateDiscount_frequentApplied() {
        prepareCommon(3L, false, 30, 8.0);
        DiscountBreakdownResponse response = discountService.calculateDiscount(3L, 1L, "1H");
        assertThat(response.getDiscountType()).isEqualTo("FREQUENT");
    }

    @Test
    @DisplayName("calculateDiscount_priorityRuleStable")
    void calculateDiscount_priorityRuleStable() {
        prepareCommon(4L, true, 70, 12.0);
        String type = discountService.resolveDiscountType(4L);
        assertThat(type).isEqualTo("FREQUENT");
    }
}
