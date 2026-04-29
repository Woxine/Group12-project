package com.group12.backend.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.group12.backend.config.BillingProperties;
import com.group12.backend.dto.BookingConfirmationEmailPayload;
import com.group12.backend.dto.CreateGuestBookingRequest;
import com.group12.backend.dto.DiscountBreakdownResponse;
import com.group12.backend.dto.BookingResponse;
import com.group12.backend.dto.CreateBookingRequest;
import com.group12.backend.dto.PayBookingRequest;
import com.group12.backend.entity.Booking;
import com.group12.backend.entity.Payment;
import com.group12.backend.entity.Scooter;
import com.group12.backend.entity.User;
import com.group12.backend.exception.BusinessException;
import com.group12.backend.exception.ErrorMessages;
import com.group12.backend.repository.BookingRepository;
import com.group12.backend.repository.PaymentCardRepository;
import com.group12.backend.repository.PaymentRepository;
import com.group12.backend.repository.ScooterRepository;
import com.group12.backend.repository.UserRepository;
import com.group12.backend.service.BillingRule;
import com.group12.backend.service.BillingService;
import com.group12.backend.service.BookingService;
import com.group12.backend.service.DiscountService;
import com.group12.backend.service.EmailNotificationService;
import com.group12.backend.service.pricing.RentalPricing;
import com.group12.backend.util.BookingTimeSupport;

/**
 * 实现预约订单创建、取消、完成和车辆状态同步的业务逻辑。
 */
@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ScooterRepository scooterRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailNotificationService emailNotificationService;

    @Autowired(required = false)
    private DiscountService discountService;

    @Autowired
    private BillingService billingService;

    @Autowired
    private BillingProperties billingProperties;

    @Autowired(required = false)
    private PaymentCardRepository paymentCardRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    @Transactional
    /**
     * 校验用户和车辆状态后创建预约订单，并将车辆标记为租用中。
     */
    public Object createBooking(CreateBookingRequest request) {
        Long userId = Long.parseLong(request.getUser_id());
        Long scooterId = Long.parseLong(request.getScooter_id());
        int durationMinutes = resolveDurationMinutes(request.getDurationMinutes(), request.getDurationCode(), request.getDuration());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorMessages.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (hasActiveBooking(userId)) {
            throw new BusinessException(ErrorMessages.ACTIVE_BOOKING_EXISTS);
        }

        if (paymentCardRepository != null && !paymentCardRepository.existsByUser_Id(userId)) {
            throw new BusinessException(ErrorMessages.PAYMENT_CARD_REQUIRED_FOR_BOOKING, HttpStatus.BAD_REQUEST);
        }

        Scooter scooter = scooterRepository.findByIdForUpdate(scooterId)
                .orElseThrow(() -> new BusinessException(ErrorMessages.SCOOTER_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (scooter.getVisible() != null && !scooter.getVisible()) {
            throw new BusinessException(ErrorMessages.SCOOTER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        if (!"AVAILABLE".equalsIgnoreCase(scooter.getStatus())) {
            throw new BusinessException(ErrorMessages.scooterUnavailable(scooter.getStatus()));
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = resolveClientStartTime(request.getStartTime(), now);
        Double durationHours = durationMinutes / 60.0;
        LocalDateTime endTime = startTime.plusMinutes(durationMinutes);

        java.util.List<Booking> overlapping = bookingRepository.findOverlappingBookings(scooterId, startTime, endTime);
        if (overlapping != null && !overlapping.isEmpty()) {
            throw new BusinessException(ErrorMessages.OVERLAPPING_BOOKING);
        }

        BigDecimal rate = scooter.getHourRate();
        BillingRule billingRule = billingService == null ? null : billingService.getCurrentRule();
        BigDecimal originalPrice = computeOriginalPrice(rate, durationHours, billingRule);
        DiscountBreakdownResponse discountBreakdown = discountService != null
                ? discountService.calculateDiscount(userId, scooterId, String.valueOf(durationMinutes) + "M")
                : null;
        BigDecimal discountMultiplier = resolveDiscountMultiplierFromBreakdown(discountBreakdown);
        BigDecimal totalPrice = originalPrice.multiply(discountMultiplier).setScale(2, RoundingMode.HALF_UP);
        BigDecimal discountAmount = originalPrice.subtract(totalPrice).setScale(2, RoundingMode.HALF_UP);
        String discountType = discountBreakdown != null && discountBreakdown.getDiscountType() != null
                ? discountBreakdown.getDiscountType()
                : "NONE";

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setScooter(scooter);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setDurationHours(durationHours);
        booking.setTotalPrice(totalPrice);
        booking.setOriginalPrice(originalPrice);
        booking.setDiscountAmount(discountAmount);
        booking.setDiscountMultiplier(discountMultiplier);
        booking.setDiscountType(discountType);
        booking.setStatus("PENDING_PAYMENT");
        booking.setPaymentDeadline(startTime.plusMinutes(resolvePendingPaymentLockMinutes()));
        if (request.getStartLat() != null) booking.setStartLat(request.getStartLat());
        if (request.getStartLng() != null) booking.setStartLng(request.getStartLng());

        Booking savedBooking;
        try {
            savedBooking = bookingRepository.save(booking);
            scooter.setStatus("RESERVED");
            scooterRepository.save(scooter);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException(ErrorMessages.BOOKING_CONCURRENT_CONFLICT, HttpStatus.CONFLICT);
        }

        return toBookingResponse(savedBooking);
    }

    /**
     * TODO(ID9): 预留未注册用户预约入口（店员代下单）。
     */
    @Override
    @Transactional
    public Object createGuestBooking(CreateGuestBookingRequest request) {
        Long salespersonId = parseId(request.getSalespersonId(), "salespersonId");
        Long guestUserId = parseGuestOwnerId(request.getGuestId());
        Long scooterId = parseId(request.getScooterId(), "scooterId");
        int durationMinutes = resolveDurationMinutes(request.getDurationMinutes(), request.getDurationCode(), request.getDuration());
        String durationCode = resolveDurationCode(request.getDurationCode(), request.getDuration(), durationMinutes);

        User salesperson = resolveUserSafely(salespersonId);
        if (salesperson != null && !isStaffOrAdmin(salesperson)) {
            throw new BusinessException(ErrorMessages.FORBIDDEN, HttpStatus.FORBIDDEN);
        }

        User guestUser = resolveOrCreateGuestUser(guestUserId, request.getGuestName());
        Long effectiveGuestUserId = guestUser.getId();
        if (effectiveGuestUserId == null) {
            throw new BusinessException(ErrorMessages.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        if (paymentCardRepository != null && !paymentCardRepository.existsByUser_Id(effectiveGuestUserId)) {
            throw new BusinessException(ErrorMessages.PAYMENT_CARD_REQUIRED_FOR_BOOKING, HttpStatus.BAD_REQUEST);
        }

        if (hasActiveBooking(effectiveGuestUserId)) {
            throw new BusinessException(ErrorMessages.ACTIVE_BOOKING_EXISTS);
        }

        Scooter scooter = resolveScooterSafely(scooterId);
        if (scooter == null) {
            throw new BusinessException(ErrorMessages.SCOOTER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        if (scooter.getVisible() != null && !scooter.getVisible()) {
            throw new BusinessException(ErrorMessages.SCOOTER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        if (!"AVAILABLE".equalsIgnoreCase(scooter.getStatus())) {
            throw new BusinessException(ErrorMessages.scooterUnavailable(scooter.getStatus()));
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = resolveClientStartTime(request.getStartTime(), now);
        Double durationHours = durationMinutes / 60.0;
        LocalDateTime endTime = startTime.plusMinutes(durationMinutes);

        if (bookingRepository != null) {
            java.util.List<Booking> overlapping = bookingRepository.findOverlappingBookings(scooterId, startTime, endTime);
            if (overlapping != null && !overlapping.isEmpty()) {
                throw new BusinessException(ErrorMessages.OVERLAPPING_BOOKING);
            }
        }

        Booking booking = new Booking();
        booking.setStatus("CONFIRMED");
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setDurationHours(durationHours);
        booking.setDiscountType("NONE");
        booking.setDiscountAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        booking.setDiscountMultiplier(BigDecimal.ONE.setScale(4, RoundingMode.HALF_UP));
        booking.setUser(guestUser);
        booking.setScooter(scooter);

        String bookingId = "GUEST-" + System.currentTimeMillis();
        if (bookingRepository != null) {
            Booking saved = bookingRepository.save(booking);
            if (saved != null && saved.getId() != null) {
                bookingId = String.valueOf(saved.getId());
            }
            scooter.setStatus("RENTED");
            scooterRepository.save(scooter);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", bookingId);
        result.put("status", booking.getStatus());
        result.put("salespersonId", String.valueOf(salespersonId));
        result.put("guestId", request.getGuestId());
        result.put("guestName", request.getGuestName());
        result.put("guestContact", request.getGuestContact());
        result.put("scooterId", String.valueOf(scooterId));
        result.put("duration", BookingTimeSupport.formatDurationLabel(durationMinutes));
        result.put("durationCode", durationCode);
        result.put("durationMinutes", durationMinutes);
        result.put("startTime", startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        result.put("endTime", endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        return result;
    }

    /**
     * TODO(ID22): 折扣预览入口，供前端下单前试算。
     */
    @Override
    public DiscountBreakdownResponse previewDiscount(String userId, String scooterId, String duration) {
        Long parsedUserId = Long.parseLong(userId);
        Long parsedScooterId = Long.parseLong(scooterId);
        if (discountService == null) {
            BillingRule billingRule = billingService.getCurrentRule();
            BigDecimal fallbackOriginal = scooterRepository.findById(parsedScooterId)
                    .map(s -> RentalPricing.computeTotal(s.getHourRate(), resolveDurationHours(duration), billingRule))
                    .orElse(BigDecimal.ZERO)
                    .setScale(2, RoundingMode.HALF_UP);
            return DiscountBreakdownResponse.of(fallbackOriginal, "NONE", BigDecimal.ZERO, fallbackOriginal, "discount.none");
        }
        return discountService.calculateDiscount(parsedUserId, parsedScooterId, duration);
    }

    @Override
    @Transactional
    public Object payBooking(String bookingId, Long authUserId, PayBookingRequest request) {
        Long id = Long.parseLong(bookingId);
        Booking booking = bookingRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new BusinessException(ErrorMessages.BOOKING_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (booking.getUser() == null || !booking.getUser().getId().equals(authUserId)) {
            throw new BusinessException(ErrorMessages.FORBIDDEN, HttpStatus.FORBIDDEN);
        }

        if (!"PENDING_PAYMENT".equalsIgnoreCase(booking.getStatus())) {
            throw new BusinessException(ErrorMessages.bookingStateChanged(booking.getStatus()), HttpStatus.CONFLICT);
        }

        if (booking.getPaymentDeadline() != null && LocalDateTime.now().isAfter(booking.getPaymentDeadline())) {
            throw new BusinessException(ErrorMessages.BOOKING_PAYMENT_EXPIRED, HttpStatus.CONFLICT);
        }

        if (paymentRepository.findByBookingId(id).isPresent()) {
            throw new BusinessException(ErrorMessages.BOOKING_ALREADY_PAID, HttpStatus.CONFLICT);
        }

        validatePaymentCardSelection(authUserId, request.getPaymentCardId());

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(booking.getTotalPrice() == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : booking.getTotalPrice());
        payment.setPaymentMethod(normalizePaymentMethod(request.getPaymentMethod()));
        payment.setTimestamp(LocalDateTime.now());
        paymentRepository.save(payment);

        booking.setStatus("CONFIRMED");
        booking.setPaymentDeadline(null);
        Booking savedBooking = bookingRepository.save(booking);

        Scooter scooter = savedBooking.getScooter();
        if (scooter != null && !"RENTED".equalsIgnoreCase(scooter.getStatus())) {
            scooter.setStatus("RENTED");
            scooterRepository.save(scooter);
        }

        sendBookingConfirmationSafely(savedBooking.getUser(), savedBooking.getScooter(), savedBooking,
                resolveDurationLabel(savedBooking.getDurationHours()));
        return toBookingResponse(savedBooking);
    }

    private void sendBookingConfirmationSafely(User user, Scooter scooter, Booking booking, String durationRequest) {
        try {
            BookingConfirmationEmailPayload payload = new BookingConfirmationEmailPayload();
            payload.setEmail(user.getEmail());
            payload.setBookingId(String.valueOf(booking.getId()));
            payload.setScooterId(String.valueOf(scooter.getId()));
            payload.setDuration(durationRequest);
            payload.setTotalPrice(booking.getTotalPrice() == null ? "" : booking.getTotalPrice().toPlainString());
            emailNotificationService.sendBookingConfirmation(payload);
        } catch (Exception ignored) {
            // 邮件失败不影响订单主流程
        }
    }

    @Override
    @Transactional
    /**
     * 取消指定预约订单，并释放对应滑板车。
     */
    public Object cancelBooking(String bookingId, Double endLat, Double endLng) {
        Long id = Long.parseLong(bookingId);
        Booking booking = bookingRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new BusinessException(ErrorMessages.BOOKING_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (!"CONFIRMED".equals(booking.getStatus()) && !"PENDING_PAYMENT".equals(booking.getStatus())) {
            throw new BusinessException(ErrorMessages.bookingStateChanged(booking.getStatus()), HttpStatus.CONFLICT);
        }

        booking.setStatus("CANCELLED");
        booking.setEndTime(LocalDateTime.now());
        booking.setPaymentDeadline(null);
        if (endLat != null) booking.setEndLat(endLat);
        if (endLng != null) booking.setEndLng(endLng);
        bookingRepository.save(booking);

        Scooter scooter = booking.getScooter();
        scooter.setStatus("AVAILABLE");
        if (endLat != null) scooter.setLocationLat(endLat);
        if (endLng != null) scooter.setLocationLng(endLng);
        scooterRepository.save(scooter);

        return "Booking cancelled successfully";
    }

    @Override
    @Transactional
    /**
     * 完成指定预约订单，并释放对应滑板车。
     */
    public Object completeBooking(String bookingId, Double endLat, Double endLng) {
        Long id = Long.parseLong(bookingId);
        Booking booking = bookingRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new BusinessException(ErrorMessages.BOOKING_NOT_FOUND, HttpStatus.NOT_FOUND));
        if (!"CONFIRMED".equals(booking.getStatus())) {
            throw new BusinessException(ErrorMessages.bookingStateChanged(booking.getStatus()), HttpStatus.CONFLICT);
        }
        booking.setStatus("COMPLETED");
        booking.setEndTime(LocalDateTime.now());
        if (endLat != null) booking.setEndLat(endLat);
        if (endLng != null) booking.setEndLng(endLng);
        bookingRepository.save(booking);
        Scooter scooter = booking.getScooter();
        scooter.setStatus("AVAILABLE");
        if (endLat != null) scooter.setLocationLat(endLat);
        if (endLng != null) scooter.setLocationLng(endLng);
        scooterRepository.save(scooter);
        return "Booking completed successfully";
    }

    private static double resolveDurationHours(String durationRequest) {
        return resolveDurationMinutes(null, null, durationRequest) / 60.0;
    }

    private static int resolveDurationMinutes(Integer durationMinutes, String durationCode, String legacyDuration) {
        if (durationMinutes == null
                && (durationCode == null || durationCode.isBlank())
                && (legacyDuration == null || legacyDuration.isBlank())) {
            throw new BusinessException("duration is required", HttpStatus.BAD_REQUEST);
        }
        int resolved = BookingTimeSupport.resolveDurationMinutes(durationMinutes, durationCode, legacyDuration);
        try {
            BookingTimeSupport.validateDurationMinutes(resolved);
        } catch (IllegalArgumentException ex) {
            throw new BusinessException(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
        Integer fromCode = BookingTimeSupport.presetMinutesByCode(durationCode);
        if (fromCode != null && fromCode.intValue() != resolved) {
            throw new BusinessException("durationCode does not match durationMinutes", HttpStatus.BAD_REQUEST);
        }
        return resolved;
    }

    private static String resolveDurationCode(String durationCode, String legacyDuration, int durationMinutes) {
        String normalized = durationCode != null && !durationCode.isBlank() ? durationCode.trim().toUpperCase()
                : (legacyDuration != null ? legacyDuration.trim().toUpperCase() : null);
        Integer presetByCode = BookingTimeSupport.presetMinutesByCode(normalized);
        if (presetByCode != null) {
            return normalized;
        }
        return BookingTimeSupport.presetCodeByMinutes(durationMinutes);
    }

    private static LocalDateTime resolveClientStartTime(String startTimeRaw, LocalDateTime now) {
        try {
            return BookingTimeSupport.parseClientStartTime(startTimeRaw, now);
        } catch (IllegalArgumentException ex) {
            throw new BusinessException(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            throw new BusinessException("startTime format is invalid", HttpStatus.BAD_REQUEST);
        }
    }

    private static BigDecimal resolveDiscountMultiplier(BigDecimal originalPrice, BigDecimal finalPrice) {
        if (originalPrice == null || finalPrice == null || originalPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ONE.setScale(4, RoundingMode.HALF_UP);
        }
        return finalPrice.divide(originalPrice, 4, RoundingMode.HALF_UP);
    }

    private static BigDecimal computeOriginalPrice(BigDecimal rate, double durationHours, BillingRule billingRule) {
        if (rate == null || durationHours <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        if (billingRule == null) {
            return rate.multiply(BigDecimal.valueOf(durationHours)).setScale(2, RoundingMode.HALF_UP);
        }
        return RentalPricing.computeTotal(rate, durationHours, billingRule);
    }

    private static BigDecimal resolveDiscountMultiplierFromBreakdown(DiscountBreakdownResponse discountBreakdown) {
        if (discountBreakdown == null) {
            return BigDecimal.ONE.setScale(4, RoundingMode.HALF_UP);
        }
        return resolveDiscountMultiplier(discountBreakdown.getOriginalPrice(), discountBreakdown.getFinalPrice());
    }

    private boolean hasActiveBooking(Long userId) {
        java.util.List<Booking> confirmedBookings = bookingRepository.findByUser_IdAndStatus(userId, "CONFIRMED");
        if (confirmedBookings != null && !confirmedBookings.isEmpty()) {
            return true;
        }
        java.util.List<Booking> pendingBookings = bookingRepository.findByUser_IdAndStatus(userId, "PENDING_PAYMENT");
        return pendingBookings != null && !pendingBookings.isEmpty();
    }

    private int resolvePendingPaymentLockMinutes() {
        Integer configured = billingProperties == null ? null : billingProperties.getPendingPaymentLockMinutes();
        return configured == null || configured < 1 ? 5 : configured;
    }

    private BookingResponse toBookingResponse(Booking booking) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String createdAtStr = booking.getStartTime() == null ? "" : booking.getStartTime().format(fmt);
        BookingResponse response = new BookingResponse(
                String.valueOf(booking.getId()),
                booking.getScooter() == null ? null : String.valueOf(booking.getScooter().getId()),
                booking.getUser() == null ? null : String.valueOf(booking.getUser().getId()),
                booking.getStatus(),
                createdAtStr);
        response.setStartTime(booking.getStartTime() == null ? null : booking.getStartTime().format(fmt));
        response.setEndTime(booking.getEndTime() == null ? null : booking.getEndTime().format(fmt));
        int durationMinutes = booking.getDurationHours() == null ? 0 : (int) Math.round(booking.getDurationHours() * 60.0);
        response.setDuration(resolveDurationLabel(booking.getDurationHours()));
        response.setDurationMinutes(durationMinutes > 0 ? durationMinutes : null);
        response.setDurationCode(durationMinutes > 0 ? BookingTimeSupport.presetCodeByMinutes(durationMinutes) : null);
        response.setTotalPrice(booking.getTotalPrice() != null ? booking.getTotalPrice().doubleValue() : null);
        response.setOriginalPrice(booking.getOriginalPrice() != null ? booking.getOriginalPrice().doubleValue() : null);
        response.setDiscountAmount(booking.getDiscountAmount() != null ? booking.getDiscountAmount().doubleValue() : null);
        response.setDiscountMultiplier(booking.getDiscountMultiplier() != null ? booking.getDiscountMultiplier().doubleValue() : null);
        response.setDiscountType(booking.getDiscountType());
        response.setPaymentDeadline(booking.getPaymentDeadline() == null ? null : booking.getPaymentDeadline().format(fmt));
        response.setStartLat(booking.getStartLat());
        response.setStartLng(booking.getStartLng());
        response.setEndLat(booking.getEndLat());
        response.setEndLng(booking.getEndLng());
        return response;
    }

    private String resolveDurationLabel(Double durationHours) {
        if (durationHours == null) {
            return null;
        }
        int durationMinutes = (int) Math.round(durationHours * 60.0);
        return BookingTimeSupport.formatDurationLabel(durationMinutes);
    }

    private void validatePaymentCardSelection(Long userId, String paymentCardId) {
        if (paymentCardRepository == null || paymentCardId == null || paymentCardId.isBlank()) {
            return;
        }
        Long cardId = parseId(paymentCardId, "paymentCardId");
        if (paymentCardRepository.findByIdAndUser_Id(cardId, userId).isEmpty()) {
            throw new BusinessException(ErrorMessages.PAYMENT_CARD_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
    }

    private String normalizePaymentMethod(String rawPaymentMethod) {
        if (rawPaymentMethod == null) {
            return "UNKNOWN";
        }
        String normalized = rawPaymentMethod.trim().toUpperCase().replace(' ', '_');
        return switch (normalized) {
            case "BANK_CARD", "CARD", "CREDIT_CARD" -> "CREDIT_CARD";
            case "ALIPAY" -> "ALIPAY";
            case "WECHAT_PAY", "WECHAT" -> "WECHAT_PAY";
            default -> normalized;
        };
    }

    private Long parseId(String raw, String fieldName) {
        try {
            return Long.parseLong(raw);
        } catch (Exception ex) {
            throw new BusinessException(fieldName + " is invalid", HttpStatus.BAD_REQUEST);
        }
    }

    private Long parseGuestOwnerId(String guestId) {
        if (guestId == null) {
            throw new BusinessException("guestId is invalid", HttpStatus.BAD_REQUEST);
        }
        String digits = guestId.replaceAll("\\D+", "");
        if (digits.isEmpty()) {
            throw new BusinessException("guestId is invalid", HttpStatus.BAD_REQUEST);
        }
        return parseId(digits, "guestId");
    }

    private User resolveOrCreateGuestUser(Long guestUserId, String guestName) {
        User guestUser = resolveUserSafely(guestUserId);
        String placeholderEmail = "guest+" + guestUserId + "@placeholder.local";
        if (guestUser == null) {
            Optional<User> byEmail = userRepository.findByEmail(placeholderEmail);
            if (byEmail != null) {
                guestUser = byEmail.orElse(null);
            }
        }
        if (guestUser != null) {
            return guestUser;
        }

        User newGuest = new User();
        newGuest.setName((guestName != null && !guestName.trim().isEmpty()) ? guestName.trim() : ("Guest " + guestUserId));
        newGuest.setEmail(placeholderEmail);
        newGuest.setPassword("N/A");
        newGuest.setRole("CUSTOMER");
        newGuest.setIsStudent(false);
        try {
            return userRepository.save(newGuest);
        } catch (DataIntegrityViolationException ex) {
            Optional<User> existing = userRepository.findByEmailIgnoreCase(placeholderEmail);
            if (existing != null && existing.isPresent()) {
                return existing.get();
            }
            throw ex;
        }
    }

    private User resolveUserSafely(Long userId) {
        if (userRepository == null) {
            return null;
        }
        Optional<User> maybeUser = userRepository.findById(userId);
        return maybeUser == null ? null : maybeUser.orElse(null);
    }

    private Scooter resolveScooterSafely(Long scooterId) {
        if (scooterRepository == null) {
            return null;
        }
        Optional<Scooter> maybeScooter = scooterRepository.findByIdForUpdate(scooterId);
        return maybeScooter == null ? null : maybeScooter.orElse(null);
    }

    private boolean isStaffOrAdmin(User user) {
        String role = user.getRole();
        return "STAFF".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role);
    }
}
