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
        Booking booking = bookingRepository.findById(Long.parseLong(bookingId))
                .orElseThrow(() -> new BusinessException(ErrorMessages.BOOKING_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (!booking.getUser().getId().equals(authUserId)) {
            throw new BusinessException(ErrorMessages.FORBIDDEN, HttpStatus.FORBIDDEN);
        }

        if (!"CONFIRMED".equalsIgnoreCase(booking.getStatus())) {
            throw new BusinessException(ErrorMessages.cannotExtendBooking(booking.getStatus()));
        }

        String durationRequest = request.getDuration();
        double extraDurationHours;
        LocalDateTime newEndTime = booking.getEndTime();

        if ("10M".equalsIgnoreCase(durationRequest)) {
            extraDurationHours = 10.0 / 60.0;
            newEndTime = newEndTime.plusMinutes(10);
        } else if ("1H".equalsIgnoreCase(durationRequest)) {
            extraDurationHours = 1.0;
            newEndTime = newEndTime.plusHours(1);
        } else if ("4H".equalsIgnoreCase(durationRequest)) {
            extraDurationHours = 4.0;
            newEndTime = newEndTime.plusHours(4);
        } else if ("1D".equalsIgnoreCase(durationRequest)) {
            extraDurationHours = 24.0;
            newEndTime = newEndTime.plusHours(24);
        } else if ("1W".equalsIgnoreCase(durationRequest)) {
            extraDurationHours = 168.0;
            newEndTime = newEndTime.plusHours(168);
        } else {
            extraDurationHours = 1.0;
            newEndTime = newEndTime.plusHours(1);
        }

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
        response.setDuration(savedBooking.getDurationHours() == null ? null : savedBooking.getDurationHours() + " hours");
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
}
