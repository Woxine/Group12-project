package com.group12.backend.sprint2.concurrency;

import org.junit.jupiter.api.Test;

/**
 * TODO(ID23): 并发创建预订测试骨架。
 */
class BookingConcurrentCreateTest {

    @Test
    void concurrentCreate_sameScooter_onlyOneSuccess() {
        // TODO(ID23): run concurrent requests on same scooter and assert only one success.
    }

    @Test
    void concurrentCreate_differentScooters_multiSuccess() {
        // TODO(ID23): assert parallel bookings on different scooters can succeed.
    }

    @Test
    void concurrentCreate_noDuplicateConfirmedBooking() {
        // TODO(ID23): assert no duplicated confirmed booking in DB after stress run.
    }
}
