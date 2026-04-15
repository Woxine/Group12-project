package com.group12.backend.sprint3state1.concurrency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.dao.DataIntegrityViolationException;

import com.group12.backend.dto.CreateBookingRequest;
import com.group12.backend.entity.Booking;
import com.group12.backend.entity.Scooter;
import com.group12.backend.entity.User;
import com.group12.backend.repository.BookingRepository;
import com.group12.backend.repository.PaymentCardRepository;
import com.group12.backend.repository.ScooterRepository;
import com.group12.backend.repository.UserRepository;
import com.group12.backend.service.BillingRule;
import com.group12.backend.service.BillingService;
import com.group12.backend.service.DiscountService;
import com.group12.backend.service.EmailNotificationService;
import com.group12.backend.service.impl.BookingServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ID23 BookingConcurrentCreate")
class BookingConcurrentCreateTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ScooterRepository scooterRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PaymentCardRepository paymentCardRepository;
    @Mock
    private EmailNotificationService emailNotificationService;
    @Mock
    private DiscountService discountService;
    @Mock
    private BillingService billingService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    @DisplayName("concurrentCreate_sameScooter_onlyOneSuccess")
    void concurrentCreate_sameScooter_onlyOneSuccess() throws Exception {
        User user = buildUser(11L);
        Scooter scooter = buildScooter(1L);
        AtomicBoolean alreadyBooked = new AtomicBoolean(false);
        AtomicLong bookingIdSeed = new AtomicLong(1000);
        List<Booking> persisted = Collections.synchronizedList(new ArrayList<>());

        when(userRepository.findById(11L)).thenReturn(Optional.of(user));
        when(paymentCardRepository.existsByUser_Id(11L)).thenReturn(true);
        when(bookingRepository.findByUser_IdAndStatus(11L, "CONFIRMED")).thenReturn(List.of());
        when(scooterRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(scooter));
        when(bookingRepository.findOverlappingBookings(anyLong(), any(), any())).thenReturn(List.of());
        when(billingService.getCurrentRule()).thenReturn(defaultRule());
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            if (!alreadyBooked.compareAndSet(false, true)) {
                throw new DataIntegrityViolationException("duplicate booking for same scooter");
            }
            booking.setId(bookingIdSeed.incrementAndGet());
            persisted.add(booking);
            return booking;
        });
        when(scooterRepository.save(any(Scooter.class))).thenAnswer(invocation -> invocation.getArgument(0));

        int parallel = 10;
        List<Object> results = runParallel(parallel, () -> bookingService.createBooking(buildRequest("11", "1", "1H")));

        long successCount = results.stream().filter(result -> !(result instanceof Throwable)).count();
        long failureCount = results.stream().filter(result -> result instanceof Throwable).count();
        long confirmedCount = persisted.stream().filter(b -> "CONFIRMED".equals(b.getStatus())).count();

        assertThat(successCount).isEqualTo(1);
        assertThat(failureCount).isEqualTo(parallel - 1L);
        assertThat(confirmedCount).isEqualTo(1);
    }

    @Test
    @DisplayName("concurrentCreate_differentScooters_multiSuccess")
    void concurrentCreate_differentScooters_multiSuccess() throws Exception {
        User user = buildUser(21L);
        when(userRepository.findById(21L)).thenReturn(Optional.of(user));
        when(paymentCardRepository.existsByUser_Id(21L)).thenReturn(true);
        when(bookingRepository.findByUser_IdAndStatus(21L, "CONFIRMED")).thenReturn(List.of());
        when(bookingRepository.findOverlappingBookings(anyLong(), any(), any())).thenReturn(List.of());
        when(billingService.getCurrentRule()).thenReturn(defaultRule());

        ConcurrentHashMap<Long, Scooter> scooterMap = new ConcurrentHashMap<>();
        for (long scooterId = 1; scooterId <= 8; scooterId++) {
            scooterMap.put(scooterId, buildScooter(scooterId));
        }

        when(scooterRepository.findByIdForUpdate(anyLong())).thenAnswer(invocation -> {
            Long scooterId = invocation.getArgument(0);
            return Optional.ofNullable(scooterMap.get(scooterId));
        });

        AtomicLong bookingIdSeed = new AtomicLong(2000);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            booking.setId(bookingIdSeed.incrementAndGet());
            return booking;
        });
        when(scooterRepository.save(any(Scooter.class))).thenAnswer(invocation -> invocation.getArgument(0));

        int parallel = 8;
        List<Object> results = runParallel(parallel, new Callable<>() {
            private final AtomicLong scooterCursor = new AtomicLong(1);

            @Override
            public Object call() {
                String scooterId = String.valueOf(scooterCursor.getAndIncrement());
                return bookingService.createBooking(buildRequest("21", scooterId, "1H"));
            }
        });

        long successCount = results.stream().filter(result -> !(result instanceof Throwable)).count();
        long failureCount = results.stream().filter(result -> result instanceof Throwable).count();
        assertThat(successCount).isEqualTo(parallel);
        assertThat(failureCount).isZero();
    }

    @Test
    @DisplayName("concurrentCreate_noDuplicateConfirmedBooking")
    void concurrentCreate_noDuplicateConfirmedBooking() throws Exception {
        User user = buildUser(31L);
        Scooter scooter = buildScooter(3L);
        AtomicBoolean alreadyBooked = new AtomicBoolean(false);
        List<Booking> persisted = Collections.synchronizedList(new ArrayList<>());
        AtomicLong bookingIdSeed = new AtomicLong(3000);

        when(userRepository.findById(31L)).thenReturn(Optional.of(user));
        when(paymentCardRepository.existsByUser_Id(31L)).thenReturn(true);
        when(bookingRepository.findByUser_IdAndStatus(31L, "CONFIRMED")).thenReturn(List.of());
        when(scooterRepository.findByIdForUpdate(3L)).thenReturn(Optional.of(scooter));
        when(bookingRepository.findOverlappingBookings(anyLong(), any(), any())).thenReturn(List.of());
        when(billingService.getCurrentRule()).thenReturn(defaultRule());

        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            if (!alreadyBooked.compareAndSet(false, true)) {
                throw new DataIntegrityViolationException("duplicate confirmed booking");
            }
            booking.setId(bookingIdSeed.incrementAndGet());
            persisted.add(booking);
            return booking;
        });
        when(scooterRepository.save(any(Scooter.class))).thenAnswer(invocation -> invocation.getArgument(0));

        int parallel = 30;
        runParallel(parallel, () -> bookingService.createBooking(buildRequest("31", "3", "1H")));

        long confirmedCount = persisted.stream()
                .filter(booking -> booking.getScooter() != null && Long.valueOf(3L).equals(booking.getScooter().getId()))
                .filter(booking -> "CONFIRMED".equals(booking.getStatus()))
                .count();

        assertThat(confirmedCount).isEqualTo(1);
        assertThat(persisted.size()).isEqualTo(1);
    }

    private List<Object> runParallel(int parallel, Callable<Object> action) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(Math.min(parallel, 12));
        CountDownLatch ready = new CountDownLatch(parallel);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<Object>> futures = new ArrayList<>();
        for (int i = 0; i < parallel; i++) {
            futures.add(executor.submit(() -> {
                ready.countDown();
                start.await(2, TimeUnit.SECONDS);
                try {
                    return action.call();
                } catch (Throwable throwable) {
                    return throwable;
                }
            }));
        }
        ready.await(2, TimeUnit.SECONDS);
        start.countDown();

        List<Object> results = new ArrayList<>();
        for (Future<Object> future : futures) {
            results.add(future.get(3, TimeUnit.SECONDS));
        }
        executor.shutdownNow();
        return results;
    }

    private CreateBookingRequest buildRequest(String userId, String scooterId, String duration) {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setUser_id(userId);
        request.setScooter_id(scooterId);
        request.setDuration(duration);
        return request;
    }

    private User buildUser(Long userId) {
        User user = new User();
        user.setId(userId);
        user.setEmail("u" + userId + "@example.com");
        user.setName("User-" + userId);
        user.setRole("CUSTOMER");
        user.setPassword("password");
        return user;
    }

    private Scooter buildScooter(Long scooterId) {
        Scooter scooter = new Scooter();
        scooter.setId(scooterId);
        scooter.setStatus("AVAILABLE");
        scooter.setVisible(true);
        scooter.setHourRate(new BigDecimal("5.00"));
        return scooter;
    }

    private BillingRule defaultRule() {
        return new BillingRule(
                new BigDecimal("24"),
                new BigDecimal("72"),
                new BigDecimal("0.85"),
                new BigDecimal("0.75"),
                LocalDateTime.now());
    }
}
