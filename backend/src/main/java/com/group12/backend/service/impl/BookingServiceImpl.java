package com.group12.backend.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.group12.backend.dto.BookingResponse;
import com.group12.backend.dto.CreateBookingRequest;
import com.group12.backend.entity.Booking;
import com.group12.backend.entity.Scooter;
import com.group12.backend.entity.User;
import com.group12.backend.exception.BusinessException;
import com.group12.backend.exception.ErrorMessages;
import com.group12.backend.repository.BookingRepository;
import com.group12.backend.repository.ScooterRepository;
import com.group12.backend.repository.UserRepository;
import com.group12.backend.service.BookingService;

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

        Scooter scooter = scooterRepository.findByIdForUpdate(scooterId)
                .orElseThrow(() -> new BusinessException(ErrorMessages.SCOOTER_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (!"AVAILABLE".equalsIgnoreCase(scooter.getStatus())) {
            throw new BusinessException(ErrorMessages.scooterUnavailable(scooter.getStatus()));
        }

        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime;
        Double durationHours;

        if ("10M".equalsIgnoreCase(durationRequest)) {
            durationHours = 10.0 / 60.0;
            endTime = startTime.plusMinutes(10);
        } else if ("1H".equalsIgnoreCase(durationRequest)) {
            durationHours = 1.0;
            endTime = startTime.plusHours(1);
        } else if ("4H".equalsIgnoreCase(durationRequest)) {
            durationHours = 4.0;
            endTime = startTime.plusHours(4);
        } else if ("1D".equalsIgnoreCase(durationRequest)) {
            durationHours = 24.0;
            endTime = startTime.plusHours(24);
        } else if ("1W".equalsIgnoreCase(durationRequest)) {
            durationHours = 168.0;
            endTime = startTime.plusHours(168);
        } else {
            durationHours = 1.0;
            endTime = startTime.plusHours(1);
        }

        java.util.List<Booking> overlapping = bookingRepository.findOverlappingBookings(scooterId, startTime, endTime);
        if (overlapping != null && !overlapping.isEmpty()) {
            throw new BusinessException(ErrorMessages.OVERLAPPING_BOOKING);
        }

        java.math.BigDecimal rate = scooter.getHourRate();
        java.math.BigDecimal totalPrice = rate.multiply(java.math.BigDecimal.valueOf(durationHours));

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setScooter(scooter);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setDurationHours(durationHours);
        booking.setTotalPrice(totalPrice);
        booking.setStatus("CONFIRMED");
        if (request.getStartLat() != null) booking.setStartLat(request.getStartLat());
        if (request.getStartLng() != null) booking.setStartLng(request.getStartLng());

        Booking savedBooking = bookingRepository.save(booking);

        scooter.setStatus("RENTED");
        scooterRepository.save(scooter);

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
        return response;
    }

    @Override
    @Transactional
    /**
     * 取消指定预约订单，并释放对应滑板车。
     */
    public Object cancelBooking(String bookingId, Double endLat, Double endLng) {
        Long id = Long.parseLong(bookingId);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorMessages.BOOKING_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (!"CONFIRMED".equals(booking.getStatus())) {
            throw new BusinessException(ErrorMessages.cannotCancelBooking(booking.getStatus()));
        }

        booking.setStatus("CANCELLED");
        booking.setEndTime(LocalDateTime.now());
        if (endLat != null) booking.setEndLat(endLat);
        if (endLng != null) booking.setEndLng(endLng);
        bookingRepository.save(booking);

        Scooter scooter = booking.getScooter();
        scooter.setStatus("AVAILABLE");
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
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorMessages.BOOKING_NOT_FOUND, HttpStatus.NOT_FOUND));
        if (!"CONFIRMED".equals(booking.getStatus())) {
            throw new BusinessException(ErrorMessages.cannotCompleteBooking(booking.getStatus()));
        }
        booking.setStatus("COMPLETED");
        booking.setEndTime(LocalDateTime.now());
        if (endLat != null) booking.setEndLat(endLat);
        if (endLng != null) booking.setEndLng(endLng);
        bookingRepository.save(booking);
        Scooter scooter = booking.getScooter();
        scooter.setStatus("AVAILABLE");
        scooterRepository.save(scooter);
        return "Booking completed successfully";
    }
}
