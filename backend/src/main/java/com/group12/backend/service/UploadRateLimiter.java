package com.group12.backend.service;

import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.group12.backend.exception.BusinessException;

/**
 * Per-user upload rate limiter using a sliding window.
 * Limits each user to a configurable number of uploads within a time window.
 */
@Component
public class UploadRateLimiter {

    private static final int MAX_UPLOADS = 5;
    private static final long WINDOW_MS = 60_000; // 1 minute

    private final ConcurrentHashMap<Long, Deque<Long>> userTimestamps = new ConcurrentHashMap<>();

    public void check(Long userId) {
        long now = System.currentTimeMillis();
        Deque<Long> timestamps = userTimestamps.computeIfAbsent(userId, k -> new java.util.ArrayDeque<>());

        synchronized (timestamps) {
            // Remove expired entries
            while (!timestamps.isEmpty() && now - timestamps.peekFirst() > WINDOW_MS) {
                timestamps.pollFirst();
            }

            if (timestamps.size() >= MAX_UPLOADS) {
                throw new BusinessException(
                        "Too many upload requests. Please wait before trying again.",
                        HttpStatus.TOO_MANY_REQUESTS);
            }

            timestamps.addLast(now);
        }
    }
}
