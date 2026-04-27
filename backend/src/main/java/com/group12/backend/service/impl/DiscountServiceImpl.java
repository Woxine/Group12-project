package com.group12.backend.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.group12.backend.config.DiscountProperties;
import com.group12.backend.dto.DiscountBreakdownResponse;
import com.group12.backend.entity.Scooter;
import com.group12.backend.entity.User;
import com.group12.backend.repository.BookingRepository;
import com.group12.backend.repository.DiscountVerificationSubmissionRepository;
import com.group12.backend.repository.ScooterRepository;
import com.group12.backend.repository.UserRepository;
import com.group12.backend.service.BillingRule;
import com.group12.backend.service.BillingService;
import com.group12.backend.service.DiscountVerificationConstants;
import com.group12.backend.service.DiscountService;
import com.group12.backend.service.pricing.RentalPricing;

/**
 * 折扣规则服务：负责按用户身份和近 7 日用车时长计算折扣。
 */
@Service
public class DiscountServiceImpl implements DiscountService {
    private static final BigDecimal DEFAULT_DISCOUNT_RATE = new BigDecimal("0.8");

    private static final String DISCOUNT_NONE = "NONE";
    private static final String DISCOUNT_FREQUENT = "FREQUENT";
    private static final String DISCOUNT_STUDENT = "STUDENT";
    private static final String DISCOUNT_SENIOR = "SENIOR";

    @Autowired(required = false)
    private UserRepository userRepository;

    @Autowired(required = false)
    private BookingRepository bookingRepository;

    @Autowired(required = false)
    private ScooterRepository scooterRepository;

    @Autowired(required = false)
    private DiscountProperties discountProperties;

    @Autowired(required = false)
    private DiscountVerificationSubmissionRepository discountVerificationSubmissionRepository;

    @Autowired(required = false)
    private BillingService billingService;

    private Clock clock = Clock.systemDefaultZone();

    @Autowired(required = false)
    public void setClock(Clock clock) {
        if (clock != null) {
            this.clock = clock;
        }
    }

    @Override
    public DiscountBreakdownResponse calculateDiscount(Long userId, Long scooterId, String duration) {
        BigDecimal originalPrice = calculateOriginalPrice(scooterId, duration);
        String discountType = resolveDiscountType(userId);
        BigDecimal finalPrice = applyDiscount(originalPrice, discountType);
        BigDecimal discountAmount = originalPrice.subtract(finalPrice).setScale(2, RoundingMode.HALF_UP);
        return DiscountBreakdownResponse.of(
                originalPrice,
                discountType,
                discountAmount,
                finalPrice,
                messageKeyFor(discountType)
        );
    }

    @Override
    public String resolveDiscountType(Long userId) {
        if (!isDiscountEnabled()) {
            return DISCOUNT_NONE;
        }
        if (isFrequentUser(userId)) {
            return DISCOUNT_FREQUENT;
        }
        if (isStudent(userId)) {
            return DISCOUNT_STUDENT;
        }
        if (isSenior(userId)) {
            return DISCOUNT_SENIOR;
        }
        return DISCOUNT_NONE;
    }

    @Override
    public boolean hasFrequentDiscount(Long userId) {
        return isDiscountEnabled() && isFrequentUser(userId);
    }

    @Override
    public boolean hasStudentDiscount(Long userId) {
        return isDiscountEnabled() && isStudent(userId);
    }

    @Override
    public boolean hasSeniorDiscount(Long userId) {
        return isDiscountEnabled() && isSenior(userId);
    }

    public boolean isFrequentUser(Long userId) {
        if (userId == null || bookingRepository == null) {
            return false;
        }
        Instant now = Instant.now(clock);
        ZoneId zoneId = clock.getZone();
        LocalDateTime toTime = LocalDateTime.ofInstant(now, zoneId);
        LocalDateTime fromTime = toTime.minusDays(getProperties().getRollingDays());
        Double totalHours = bookingRepository.sumCompletedDurationHours(userId, fromTime, toTime);
        double safeHours = totalHours == null ? 0.0 : totalHours;
        return safeHours >= getProperties().getHeavyUserHoursThreshold();
    }

    public boolean isStudent(Long userId) {
        if (userId == null || discountVerificationSubmissionRepository == null) {
            return false;
        }
        return discountVerificationSubmissionRepository
                .findTopByUser_IdAndTypeAndStatusOrderByVersionDesc(
                        userId,
                        DiscountVerificationConstants.TYPE_STUDENT,
                        DiscountVerificationConstants.STATUS_APPROVED
                )
                .isPresent();
    }

    public boolean isSenior(Long userId) {
        if (userId == null || userRepository == null) {
            return false;
        }
        Optional<User> user = userRepository.findById(userId);
        boolean seniorByAge = user.map(u -> u.getAge() != null && u.getAge() >= getProperties().getElderlyMinAge()).orElse(false);
        if (seniorByAge) {
            return true;
        }
        if (discountVerificationSubmissionRepository == null) {
            return false;
        }
        return discountVerificationSubmissionRepository
                .findTopByUser_IdAndTypeAndStatusOrderByVersionDesc(
                        userId,
                        DiscountVerificationConstants.TYPE_SENIOR,
                        DiscountVerificationConstants.STATUS_APPROVED
                )
                .isPresent();
    }

    public BigDecimal applyDiscount(BigDecimal origin, String discountType) {
        BigDecimal safeOrigin = origin == null ? BigDecimal.ZERO : origin;
        if (!isDiscountEnabled() || !isDiscountTypeEligible(discountType)) {
            return safeOrigin.setScale(2, RoundingMode.HALF_UP);
        }
        BillingRule rule = billingService == null ? null : billingService.getCurrentRule();
        BigDecimal rate = resolveRateForType(discountType, rule);
        return safeOrigin.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal resolveRateForType(String discountType, BillingRule rule) {
        if (rule != null) {
            if (DISCOUNT_STUDENT.equalsIgnoreCase(discountType) && rule.studentDiscountRate() != null) {
                return rule.studentDiscountRate();
            }
            if (DISCOUNT_SENIOR.equalsIgnoreCase(discountType) && rule.seniorDiscountRate() != null) {
                return rule.seniorDiscountRate();
            }
            if (DISCOUNT_FREQUENT.equalsIgnoreCase(discountType) && rule.frequentDiscountRate() != null) {
                return rule.frequentDiscountRate();
            }
        }
        BigDecimal fallbackRate = getProperties().getRate();
        return fallbackRate == null ? DEFAULT_DISCOUNT_RATE : fallbackRate;
    }

    private BigDecimal calculateOriginalPrice(Long scooterId, String duration) {
        if (scooterId == null || scooterRepository == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        Optional<Scooter> scooter = scooterRepository.findById(scooterId);
        if (scooter.isEmpty() || scooter.get().getHourRate() == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        double durationHours = resolveDurationHours(duration);
        BillingRule rule = billingService == null ? null : billingService.getCurrentRule();
        if (rule == null) {
            BigDecimal base = scooter.get().getHourRate().multiply(BigDecimal.valueOf(durationHours));
            return base.setScale(2, RoundingMode.HALF_UP);
        }
        return RentalPricing.computeTotal(scooter.get().getHourRate(), durationHours, rule);
    }

    private static double resolveDurationHours(String duration) {
        if ("10M".equalsIgnoreCase(duration)) {
            return 10.0 / 60.0;
        }
        if ("1H".equalsIgnoreCase(duration)) {
            return 1.0;
        }
        if ("4H".equalsIgnoreCase(duration)) {
            return 4.0;
        }
        if ("1D".equalsIgnoreCase(duration)) {
            return 24.0;
        }
        if ("1W".equalsIgnoreCase(duration)) {
            return 168.0;
        }
        return 1.0;
    }

    private static boolean isDiscountTypeEligible(String discountType) {
        return DISCOUNT_STUDENT.equalsIgnoreCase(discountType)
                || DISCOUNT_SENIOR.equalsIgnoreCase(discountType)
                || DISCOUNT_FREQUENT.equalsIgnoreCase(discountType);
    }

    private static String messageKeyFor(String discountType) {
        if (DISCOUNT_STUDENT.equalsIgnoreCase(discountType)) {
            return "discount.student";
        }
        if (DISCOUNT_SENIOR.equalsIgnoreCase(discountType)) {
            return "discount.senior";
        }
        if (DISCOUNT_FREQUENT.equalsIgnoreCase(discountType)) {
            return "discount.frequent";
        }
        return "discount.none";
    }

    private boolean isDiscountEnabled() {
        return getProperties().isEnabled();
    }

    private DiscountProperties getProperties() {
        if (discountProperties == null) {
            discountProperties = new DiscountProperties();
        }
        return discountProperties;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setBookingRepository(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public void setScooterRepository(ScooterRepository scooterRepository) {
        this.scooterRepository = scooterRepository;
    }

    public void setDiscountProperties(DiscountProperties discountProperties) {
        this.discountProperties = discountProperties;
    }

    public void setDiscountVerificationSubmissionRepository(
            DiscountVerificationSubmissionRepository discountVerificationSubmissionRepository) {
        this.discountVerificationSubmissionRepository = discountVerificationSubmissionRepository;
    }
}
