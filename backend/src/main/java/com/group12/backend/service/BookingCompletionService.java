package com.group12.backend.service;

import com.group12.backend.entity.Booking;
import com.group12.backend.entity.Scooter;
import com.group12.backend.repository.BookingRepository;
import com.group12.backend.repository.ScooterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingCompletionService {

    private static final Logger log = LoggerFactory.getLogger(BookingCompletionService.class);

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ScooterRepository scooterRepository;

    /**
     * Each booking is processed in its own independent transaction.
     * A failure here only rolls back this single booking, not the whole batch.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void completeSingleBooking(Booking booking) {
        Booking fresh = bookingRepository.findById(booking.getId())
                .orElseThrow(() -> new IllegalStateException("Booking not found: " + booking.getId()));

        if (!"CONFIRMED".equals(fresh.getStatus())) {
            log.warn("BookingScheduler: Booking ID {} is no longer CONFIRMED (status={}). Skipping.",
                    fresh.getId(), fresh.getStatus());
            return;
        }

        fresh.setStatus("COMPLETED");
        bookingRepository.save(fresh);

        Scooter scooter = fresh.getScooter();
        if (scooter != null && !"AVAILABLE".equals(scooter.getStatus())) {
            scooter.setStatus("AVAILABLE");
            scooterRepository.save(scooter);
            log.info("BookingScheduler: Released Scooter ID: {}", scooter.getId());
        }

        log.info("BookingScheduler: Auto-completed Booking ID: {}", fresh.getId());
    }
}
