package com.group12.backend.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.group12.backend.dto.BookingResponse;
import com.group12.backend.dto.ExtendBookingRequest;
import com.group12.backend.entity.Booking;
import com.group12.backend.exception.BusinessException;
import com.group12.backend.exception.ErrorMessages;
import com.group12.backend.repository.BookingRepository;
import com.group12.backend.service.BillingRule;
import com.group12.backend.service.BillingService;
import com.group12.backend.service.BookingExtensionService;
import com.group12.backend.service.pricing.RentalPricing;
import com.group12.backend.util.BookingTimeSupport;

/**
 * TODO(ID10&11): 预订延长实现骨架（仅 TODO，不含业务实现）。
 */
@Service
public class BookingExtensionServiceImpl implements BookingExtensionService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BillingService billingService;

    @Override
    @Transactional
    public Object extendBooking(String bookingId, ExtendBookingRequest request, Long authUserId) {
        Booking booking = bookingRepository.findByIdForUpdate(Long.parseLong(bookingId))
                .orElseThrow(() -> new BusinessException(ErrorMessages.BOOKING_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (!booking.getUser().getId().equals(authUserId)) {
            throw new BusinessException(ErrorMessages.FORBIDDEN, HttpStatus.FORBIDDEN);
        }

        if (!"CONFIRMED".equalsIgnoreCase(booking.getStatus())) {
            throw new BusinessException(ErrorMessages.bookingStateChanged(booking.getStatus()), HttpStatus.CONFLICT);
        }

        int extraDurationMinutes = resolveDurationMinutes(request);
        double extraDurationHours;
        LocalDateTime newEndTime = booking.getEndTime();
        extraDurationHours = extraDurationMinutes / 60.0;
        newEndTime = newEndTime.plusMinutes(extraDurationMinutes);

        // Check for overlap excluding current booking
        List<Booking> overlapping = bookingRepository.findOverlappingBookings(booking.getScooter().getId(), booking.getStartTime(), newEndTime);
        if (overlapping != null) {
            overlapping = overlapping.stream()
                .filter(b -> !b.getId().equals(booking.getId()))
                .collect(Collectors.toList());
            if (!overlapping.isEmpty()) {
                throw new BusinessException(ErrorMessages.OVERLAPPING_BOOKING);
            }
        }

        // Update fields
        booking.setEndTime(newEndTime);
        double newDurationHours = booking.getDurationHours() + extraDurationHours;
        booking.setDurationHours(newDurationHours);
        BillingRule billingRule = billingService.getCurrentRule();
        BigDecimal newOriginalPrice = RentalPricing.computeTotal(booking.getScooter().getHourRate(), newDurationHours, billingRule);
        BigDecimal multiplier = booking.getDiscountMultiplier() == null
                ? BigDecimal.ONE
                : booking.getDiscountMultiplier();
        BigDecimal newTotalPrice = newOriginalPrice.multiply(multiplier).setScale(2, java.math.RoundingMode.HALF_UP);
        booking.setTotalPrice(newTotalPrice);
        booking.setOriginalPrice(newOriginalPrice);
        booking.setDiscountAmount(newOriginalPrice.subtract(newTotalPrice).setScale(2, java.math.RoundingMode.HALF_UP));

        Booking savedBooking = bookingRepository.save(booking);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String createdAtStr = savedBooking.getStartTime() == null ? "" : savedBooking.getStartTime().format(fmt);
        BookingResponse response = new BookingResponse(
                String.valueOf(savedBooking.getId()),
                String.valueOf(savedBooking.getScooter().getId()),
                String.valueOf(savedBooking.getUser().getId()),
                savedBooking.getStatus(),
                createdAtStr
        );
        response.setStartTime(savedBooking.getStartTime() == null ? null : savedBooking.getStartTime().format(fmt));
        response.setEndTime(savedBooking.getEndTime() == null ? null : savedBooking.getEndTime().format(fmt));
        int totalMinutes = savedBooking.getDurationHours() == null ? 0 : (int) Math.round(savedBooking.getDurationHours() * 60.0);
        response.setDuration(totalMinutes > 0 ? BookingTimeSupport.formatDurationLabel(totalMinutes) : null);
        response.setDurationMinutes(totalMinutes > 0 ? totalMinutes : null);
        response.setDurationCode(totalMinutes > 0 ? BookingTimeSupport.presetCodeByMinutes(totalMinutes) : null);
        response.setTotalPrice(savedBooking.getTotalPrice() == null ? null : savedBooking.getTotalPrice().doubleValue());
        response.setOriginalPrice(savedBooking.getOriginalPrice() == null ? null : savedBooking.getOriginalPrice().doubleValue());
        response.setDiscountAmount(savedBooking.getDiscountAmount() == null ? null : savedBooking.getDiscountAmount().doubleValue());
        response.setDiscountMultiplier(savedBooking.getDiscountMultiplier() == null ? null : savedBooking.getDiscountMultiplier().doubleValue());
        response.setDiscountType(savedBooking.getDiscountType());
        response.setStartLat(savedBooking.getStartLat());
        response.setStartLng(savedBooking.getStartLng());
        response.setEndLat(savedBooking.getEndLat());
        response.setEndLng(savedBooking.getEndLng());

        return response;
    }

    private int resolveDurationMinutes(ExtendBookingRequest request) {
        if (request.getDurationMinutes() == null
                && (request.getDurationCode() == null || request.getDurationCode().isBlank())
                && (request.getDuration() == null || request.getDuration().isBlank())) {
            throw new BusinessException("duration is required", HttpStatus.BAD_REQUEST);
        }
        int resolved = BookingTimeSupport.resolveDurationMinutes(
                request.getDurationMinutes(),
                request.getDurationCode(),
                request.getDuration());
        try {
            BookingTimeSupport.validateDurationMinutes(resolved);
        } catch (IllegalArgumentException ex) {
            throw new BusinessException(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
        Integer fromCode = BookingTimeSupport.presetMinutesByCode(request.getDurationCode());
        if (fromCode != null && fromCode.intValue() != resolved) {
            throw new BusinessException("durationCode does not match durationMinutes", HttpStatus.BAD_REQUEST);
        }
        return resolved;
    }
}
