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

import com.group12.backend.dto.BookingConfirmationEmailPayload;
import com.group12.backend.dto.CreateGuestBookingRequest;
import com.group12.backend.dto.DiscountBreakdownResponse;
import com.group12.backend.dto.BookingResponse;
import com.group12.backend.dto.CreateBookingRequest;
import com.group12.backend.entity.Booking;
import com.group12.backend.entity.Scooter;
import com.group12.backend.entity.User;
import com.group12.backend.exception.BusinessException;
import com.group12.backend.exception.ErrorMessages;
import com.group12.backend.repository.BookingRepository;
import com.group12.backend.repository.PaymentCardRepository;
import com.group12.backend.repository.ScooterRepository;
import com.group12.backend.repository.UserRepository;
import com.group12.backend.service.BillingRule;
import com.group12.backend.service.BillingService;
import com.group12.backend.service.BookingService;
import com.group12.backend.service.DiscountService;
import com.group12.backend.service.EmailNotificationService;
import com.group12.backend.service.pricing.RentalPricing;

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

    @Autowired(required = false)
    private PaymentCardRepository paymentCardRepository;

    @Override
    @Transactional
    /**
     * 校验用户和车辆状态后创建预约订单，并将车辆标记为租用中。
     */
    public Object createBooking(CreateBookingRequest request) {
        Long userId = Long.parseLong(request.getUser_id());
        Long scooterId = Long.parseLong(request.getScooter_id());
        String durationRequest = request.getDuration();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorMessages.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        java.util.List<Booking> activeBookings = bookingRepository.findByUser_IdAndStatus(userId, "CONFIRMED");
        if (activeBookings != null && !activeBookings.isEmpty()) {
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

        LocalDateTime startTime = LocalDateTime.now();
        Double durationHours = resolveDurationHours(durationRequest);
        LocalDateTime endTime = startTime.plusMinutes(Math.round(durationHours * 60));

        java.util.List<Booking> overlapping = bookingRepository.findOverlappingBookings(scooterId, startTime, endTime);
        if (overlapping != null && !overlapping.isEmpty()) {
            throw new BusinessException(ErrorMessages.OVERLAPPING_BOOKING);
        }

        BigDecimal rate = scooter.getHourRate();
        BillingRule billingRule = billingService.getCurrentRule();
        BigDecimal originalPrice = RentalPricing.computeTotal(rate, durationHours, billingRule);
        DiscountBreakdownResponse discountBreakdown = discountService != null
                ? discountService.calculateDiscount(userId, scooterId, durationRequest)
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
        booking.setStatus("CONFIRMED");
        if (request.getStartLat() != null) booking.setStartLat(request.getStartLat());
        if (request.getStartLng() != null) booking.setStartLng(request.getStartLng());

        Booking savedBooking;
        try {
            savedBooking = bookingRepository.save(booking);
            scooter.setStatus("RENTED");
            scooterRepository.save(scooter);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException(ErrorMessages.BOOKING_CONCURRENT_CONFLICT, HttpStatus.CONFLICT);
        }

        sendBookingConfirmationSafely(user, scooter, savedBooking, durationRequest);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String createdAtStr = savedBooking.getStartTime() == null ? "" : savedBooking.getStartTime().format(fmt);
        BookingResponse response = new BookingResponse(
            String.valueOf(savedBooking.getId()),
            String.valueOf(scooter.getId()),
            String.valueOf(user.getId()),
            savedBooking.getStatus(),
            createdAtStr
        );
        response.setTotalPrice(savedBooking.getTotalPrice() != null ? savedBooking.getTotalPrice().doubleValue() : null);
        response.setOriginalPrice(savedBooking.getOriginalPrice() != null ? savedBooking.getOriginalPrice().doubleValue() : null);
        response.setDiscountAmount(savedBooking.getDiscountAmount() != null ? savedBooking.getDiscountAmount().doubleValue() : null);
        response.setDiscountMultiplier(savedBooking.getDiscountMultiplier() != null ? savedBooking.getDiscountMultiplier().doubleValue() : null);
        response.setDiscountType(savedBooking.getDiscountType());
        return response;
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
        String durationRequest = request.getDuration();

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

        java.util.List<Booking> activeBookings = bookingRepository.findByUser_IdAndStatus(effectiveGuestUserId, "CONFIRMED");
        if (activeBookings != null && !activeBookings.isEmpty()) {
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

        LocalDateTime startTime = LocalDateTime.now();
        Double durationHours = resolveDurationHours(durationRequest);
        LocalDateTime endTime = startTime.plusMinutes(Math.round(durationHours * 60));

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
        result.put("duration", durationRequest);
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

        if (!"CONFIRMED".equals(booking.getStatus())) {
            throw new BusinessException(ErrorMessages.bookingStateChanged(booking.getStatus()), HttpStatus.CONFLICT);
        }

        booking.setStatus("CANCELLED");
        booking.setEndTime(LocalDateTime.now());
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
        if ("10M".equalsIgnoreCase(durationRequest)) {
            return 10.0 / 60.0;
        }
        if ("1H".equalsIgnoreCase(durationRequest)) {
            return 1.0;
        }
        if ("4H".equalsIgnoreCase(durationRequest)) {
            return 4.0;
        }
        if ("1D".equalsIgnoreCase(durationRequest)) {
            return 24.0;
        }
        if ("1W".equalsIgnoreCase(durationRequest)) {
            return 168.0;
        }
        return 1.0;
    }

    private static BigDecimal resolveDiscountMultiplier(BigDecimal originalPrice, BigDecimal finalPrice) {
        if (originalPrice == null || finalPrice == null || originalPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ONE.setScale(4, RoundingMode.HALF_UP);
        }
        return finalPrice.divide(originalPrice, 4, RoundingMode.HALF_UP);
    }

    private static BigDecimal resolveDiscountMultiplierFromBreakdown(DiscountBreakdownResponse discountBreakdown) {
        if (discountBreakdown == null) {
            return BigDecimal.ONE.setScale(4, RoundingMode.HALF_UP);
        }
        return resolveDiscountMultiplier(discountBreakdown.getOriginalPrice(), discountBreakdown.getFinalPrice());
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
