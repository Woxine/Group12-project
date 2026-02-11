package com.group12.backend.task;

import com.group12.backend.entity.Booking;
import com.group12.backend.entity.Scooter;
import com.group12.backend.repository.BookingRepository;
import com.group12.backend.repository.ScooterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class BookingScheduler {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ScooterRepository scooterRepository;

    /**
     * Check for expired bookings every minute
     * If current time > booking.endTime, modify booking status to COMPLETED and scooter status to AVAILABLE
     */
    @Scheduled(fixedRate = 60000) // Executed once every minute
    @Transactional
    public void checkAndCompleteExpiredBookings() {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> expiredBookings = bookingRepository.findByStatusAndEndTimeBefore("CONFIRMED", now);

        if (!expiredBookings.isEmpty()) {
            System.out.println("BookingScheduler: Found " + expiredBookings.size() + " expired bookings. Processing...");
            for (Booking booking : expiredBookings) {
                // 1. Update Booking status
                booking.setStatus("COMPLETED");
                bookingRepository.save(booking);

                // 2. Update Scooter status
                Scooter scooter = booking.getScooter();
                if (scooter != null) {
                    scooter.setStatus("AVAILABLE");
                    scooterRepository.save(scooter);
                }
                
                System.out.println("BookingScheduler: Auto-completed Booking ID: " + booking.getId() + ", Released Scooter ID: " + (scooter != null ? scooter.getId() : "N/A"));
            }
        }
    }
}
