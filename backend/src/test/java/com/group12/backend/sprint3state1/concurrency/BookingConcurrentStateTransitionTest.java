package com.group12.backend.sprint3state1.concurrency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.group12.backend.dto.ExtendBookingRequest;
import com.group12.backend.entity.Booking;
import com.group12.backend.entity.Scooter;
import com.group12.backend.entity.User;
import com.group12.backend.exception.BusinessException;
import com.group12.backend.repository.BookingRepository;
import com.group12.backend.repository.ScooterRepository;
import com.group12.backend.service.BillingRule;
import com.group12.backend.service.BillingService;
import com.group12.backend.service.BookingCompletionService;
import com.group12.backend.service.impl.BookingExtensionServiceImpl;
import com.group12.backend.service.impl.BookingServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ID23 BookingConcurrentStateTransition")
class BookingConcurrentStateTransitionTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ScooterRepository scooterRepository;
    @Mock
    private BillingService billingService;

    @InjectMocks
    private BookingServiceImpl bookingService;
    @InjectMocks
    private BookingExtensionServiceImpl bookingExtensionService;
    @InjectMocks
    private BookingCompletionService bookingCompletionService;

    @Test
    @DisplayName("concurrentCancelAndComplete_sameBooking_idempotent")
    void concurrentCancelAndComplete_sameBooking_idempotent() throws Exception {
        Booking booking = buildConfirmedBooking(501L, 1001L);
        when(bookingRepository.findByIdForUpdate(501L)).thenAnswer(invocation -> Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(scooterRepository.save(any(Scooter.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<Object> results = runParallel(
                () -> bookingService.cancelBooking("501", null, null),
                () -> bookingService.completeBooking("501", null, null));

        long successCount = results.stream().filter(result -> !(result instanceof Throwable)).count();
        long conflictCount = results.stream().filter(this::isConflictFailure).count();
        assertThat(successCount).isEqualTo(1);
        assertThat(conflictCount).isEqualTo(1);
        assertThat(booking.getStatus()).isIn("CANCELLED", "COMPLETED");
        assertThat(booking.getScooter().getStatus()).isEqualTo("AVAILABLE");
    }

    @Test
    @DisplayName("concurrentExtendAndComplete_consistentEndTime")
    void concurrentExtendAndComplete_consistentEndTime() throws Exception {
        Booking booking = buildConfirmedBooking(601L, 2001L);
        LocalDateTime testStart = LocalDateTime.now();
        LocalDateTime originalEndTime = booking.getEndTime();
        User owner = booking.getUser();

        when(bookingRepository.findByIdForUpdate(601L)).thenAnswer(invocation -> Optional.of(booking));
        lenient().when(bookingRepository.findById(601L)).thenAnswer(invocation -> Optional.of(booking));
        lenient().when(bookingRepository.findOverlappingBookings(anyLong(), any(), any())).thenReturn(List.of());
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(scooterRepository.save(any(Scooter.class))).thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(billingService.getCurrentRule()).thenReturn(defaultRule());

        ExtendBookingRequest extendRequest = new ExtendBookingRequest();
        extendRequest.setDuration("1H");

        List<Object> results = runParallel(
                () -> bookingExtensionService.extendBooking("601", extendRequest, owner.getId()),
                () -> bookingService.completeBooking("601", null, null));

        long completeSuccess = results.stream()
                .filter(result -> "Booking completed successfully".equals(result))
                .count();
        assertThat(completeSuccess).isEqualTo(1);
        assertThat(booking.getStatus()).isEqualTo("COMPLETED");
        assertThat(booking.getScooter().getStatus()).isEqualTo("AVAILABLE");
        assertThat(booking.getEndTime()).isNotNull();
        assertThat(booking.getEndTime()).isAfterOrEqualTo(testStart.minusSeconds(1));
        assertThat(booking.getEndTime()).isBeforeOrEqualTo(originalEndTime.plusHours(1));
    }

    @Test
    @DisplayName("concurrentOperations_scooterStatusConsistent")
    void concurrentOperations_scooterStatusConsistent() throws Exception {
        Booking booking = buildConfirmedBooking(701L, 3001L);
        when(bookingRepository.findByIdForUpdate(701L)).thenAnswer(invocation -> Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(scooterRepository.save(any(Scooter.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<Object> results = runParallel(
                () -> bookingService.cancelBooking("701", null, null),
                () -> {
                    bookingCompletionService.completeSingleBooking(booking);
                    return "scheduler-complete";
                });

        long successCount = results.stream().filter(result -> !(result instanceof Throwable)).count();
        assertThat(successCount).isGreaterThanOrEqualTo(1);
        assertThat(booking.getStatus()).isIn("CANCELLED", "COMPLETED");
        assertThat(booking.getScooter().getStatus()).isEqualTo("AVAILABLE");
    }

    private List<Object> runParallel(Callable<Object> left, Callable<Object> right) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<Object>> futures = new ArrayList<>();
        futures.add(submitTask(executor, ready, start, left));
        futures.add(submitTask(executor, ready, start, right));
        ready.await(2, TimeUnit.SECONDS);
        start.countDown();

        List<Object> results = new ArrayList<>();
        for (Future<Object> future : futures) {
            results.add(future.get(3, TimeUnit.SECONDS));
        }
        executor.shutdownNow();
        return results;
    }

    private Future<Object> submitTask(
            ExecutorService executor, CountDownLatch ready, CountDownLatch start, Callable<Object> callable) {
        return executor.submit(() -> {
            ready.countDown();
            start.await(2, TimeUnit.SECONDS);
            try {
                return callable.call();
            } catch (Throwable throwable) {
                return throwable;
            }
        });
    }

    private Booking buildConfirmedBooking(Long bookingId, Long scooterId) {
        User user = new User();
        user.setId(9001L);
        user.setEmail("customer@example.com");
        user.setName("customer");
        user.setPassword("pwd");
        user.setRole("CUSTOMER");

        Scooter scooter = new Scooter();
        scooter.setId(scooterId);
        scooter.setVisible(true);
        scooter.setStatus("RENTED");
        scooter.setHourRate(new BigDecimal("6.00"));

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setUser(user);
        booking.setScooter(scooter);
        booking.setStatus("CONFIRMED");
        booking.setStartTime(LocalDateTime.now().minusMinutes(10));
        booking.setEndTime(LocalDateTime.now().plusMinutes(50));
        booking.setDurationHours(1.0);
        booking.setDiscountMultiplier(BigDecimal.ONE);
        booking.setTotalPrice(new BigDecimal("6.00"));
        booking.setOriginalPrice(new BigDecimal("6.00"));
        booking.setDiscountAmount(BigDecimal.ZERO);
        booking.setDiscountType("NONE");
        return booking;
    }

    private BillingRule defaultRule() {
        return new BillingRule(
                new BigDecimal("24"),
                new BigDecimal("72"),
                new BigDecimal("0.85"),
                new BigDecimal("0.75"),
                LocalDateTime.now());
    }

    private boolean isConflictFailure(Object result) {
        if (!(result instanceof BusinessException businessException)) {
            return false;
        }
        return businessException.getStatus() != null && businessException.getStatus().value() == 409;
    }
}
