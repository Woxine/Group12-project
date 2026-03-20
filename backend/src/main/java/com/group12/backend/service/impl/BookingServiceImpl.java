package com.group12.backend.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.group12.backend.dto.BookingResponse;
import com.group12.backend.dto.CreateBookingRequest;
import com.group12.backend.entity.Booking;
import com.group12.backend.entity.Scooter;
import com.group12.backend.entity.User;
import com.group12.backend.repository.BookingRepository;
import com.group12.backend.repository.ScooterRepository;
import com.group12.backend.repository.UserRepository;
import com.group12.backend.service.BookingService;

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
    public Object createBooking(CreateBookingRequest request) {
        Long userId = Long.parseLong(request.getUser_id());
        Long scooterId = Long.parseLong(request.getScooter_id());
        String durationRequest = request.getDuration();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        java.util.List<Booking> activeBookings = bookingRepository.findByUser_IdAndStatus(userId, "CONFIRMED");
        if (activeBookings != null && !activeBookings.isEmpty()) {
            throw new RuntimeException("You already have an active booking. Please cancel it or wait until it ends before booking again.");
        }

        Scooter scooter = scooterRepository.findByIdForUpdate(scooterId)
                .orElseThrow(() -> new RuntimeException("Scooter not found"));

        if (!"AVAILABLE".equalsIgnoreCase(scooter.getStatus())) {
            throw new RuntimeException("Scooter is not available (Current status: " + scooter.getStatus() + ")");
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
            throw new RuntimeException("Scooter has overlapping reservation. Please try another scooter or time.");
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
    public Object cancelBooking(String bookingId, Double endLat, Double endLng) {
        Long id = Long.parseLong(bookingId);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!"CONFIRMED".equals(booking.getStatus())) {
            throw new RuntimeException("Cannot cancel booking with status: " + booking.getStatus());
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
    public Object completeBooking(String bookingId, Double endLat, Double endLng) {
        Long id = Long.parseLong(bookingId);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        if (!"CONFIRMED".equals(booking.getStatus())) {
            throw new RuntimeException("Cannot complete booking with status: " + booking.getStatus());
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
