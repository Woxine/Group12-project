package com.group12.backend.task;

import com.group12.backend.entity.Booking;
import com.group12.backend.repository.BookingRepository;
import com.group12.backend.service.BookingCompletionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class BookingScheduler {

    private static final Logger log = LoggerFactory.getLogger(BookingScheduler.class);

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingCompletionService bookingCompletionService;

    @Scheduled(fixedRate = 60000)
    public void checkAndCompleteExpiredBookings() {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> expiredPendingPayments =
                bookingRepository.findByStatusAndPaymentDeadlineBefore("PENDING_PAYMENT", now);
        List<Booking> expiredBookings =
                bookingRepository.findByStatusAndEndTimeBefore("CONFIRMED", now);

        if (expiredPendingPayments.isEmpty() && expiredBookings.isEmpty()) {
            return;
        }

        log.info("BookingScheduler: Found {} expired pending payment booking(s) and {} expired confirmed booking(s). Processing...",
                expiredPendingPayments.size(), expiredBookings.size());

        int successCount = 0;
        int failCount = 0;

        for (Booking booking : expiredPendingPayments) {
            try {
                bookingCompletionService.expirePendingPaymentBooking(booking.getId());
                successCount++;
            } catch (Exception e) {
                failCount++;
                log.error("BookingScheduler: Failed to expire pending payment Booking ID: {}. Error: {}",
                        booking.getId(), e.getMessage(), e);
            }
        }

        for (Booking booking : expiredBookings) {
            try {
                bookingCompletionService.completeSingleBooking(booking);
                successCount++;
            } catch (Exception e) {
                failCount++;
                log.error("BookingScheduler: Failed to complete Booking ID: {}. Error: {}",
                        booking.getId(), e.getMessage(), e);
            }
        }

        log.info("BookingScheduler: Completed {} booking(s), failed {}.", successCount, failCount);
    }
}
