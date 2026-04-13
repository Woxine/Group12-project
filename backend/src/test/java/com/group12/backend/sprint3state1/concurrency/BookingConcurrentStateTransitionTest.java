package com.group12.backend.sprint3state1.concurrency;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ID23 BookingConcurrentStateTransition")
class BookingConcurrentStateTransitionTest {

    @Test
    @DisplayName("concurrentCancelAndComplete_sameBooking_idempotent")
    void concurrentCancelAndComplete_sameBooking_idempotent() {
        org.junit.jupiter.api.Assertions.fail(
                "TODO(ID23): implement concurrent state transition test for cancel/complete idempotency.");
    }
}
