package com.group12.backend.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

class BookingTimeSupportTest {
    private static final DateTimeFormatter CLIENT_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Test
    void parseClientStartTime_acceptsCurrentMinuteGraceForStartNow() {
        LocalDateTime now = LocalDateTime.of(2026, 5, 7, 20, 51, 30);
        String clientStartTime = now.minusMinutes(4).format(CLIENT_TIME);

        LocalDateTime parsed = BookingTimeSupport.parseClientStartTime(clientStartTime, now);

        assertThat(parsed).isEqualTo(now.withSecond(0).withNano(0));
    }
}
