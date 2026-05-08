package com.group12.backend.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.group12.backend.config.PaymentCardBinLookupProperties;
import com.group12.backend.dto.BinLookupResponse;
import com.group12.backend.exception.BusinessException;
import com.group12.backend.exception.ErrorMessages;

@Service
public class PaymentCardBinLookupService {
    private static final List<LocalBinEntry> LOCAL_BIN_CATALOG = List.of(
            new LocalBinEntry("411111", "VISA", "SIMULATED VISA ISSUER", "CREDIT", "GB"),
            new LocalBinEntry("424242", "VISA", "SIMULATED VISA TEST BANK", "CREDIT", "GB"),
            new LocalBinEntry("401288", "VISA", "SIMULATED VISA COMMERCIAL", "CREDIT", "US"),
            new LocalBinEntry("400000", "VISA", "SIMULATED VISA BANK", "DEBIT", "GB"),
            new LocalBinEntry("555555", "MASTERCARD", "SIMULATED MASTERCARD ISSUER", "CREDIT", "GB"),
            new LocalBinEntry("510510", "MASTERCARD", "SIMULATED MASTERCARD BANK", "CREDIT", "US"),
            new LocalBinEntry("222100", "MASTERCARD", "SIMULATED MASTERCARD 2-SERIES", "CREDIT", "GB"),
            new LocalBinEntry("378282", "AMEX", "AMERICAN EXPRESS TEST ISSUER", "CREDIT", "US"),
            new LocalBinEntry("371449", "AMEX", "AMERICAN EXPRESS SIMULATION", "CREDIT", "US"),
            new LocalBinEntry("356600", "JCB", "JCB TEST ISSUER", "CREDIT", "JP"),
            new LocalBinEntry("353011", "JCB", "JCB SIMULATION BANK", "CREDIT", "JP"),
            new LocalBinEntry("620000", "UNIONPAY", "UNIONPAY SIMULATION BANK", "DEBIT", "CN"),
            new LocalBinEntry("622126", "UNIONPAY", "UNIONPAY TEST ISSUER", "DEBIT", "CN"));

    private final PaymentCardBinLookupProperties properties;
    private final ConcurrentHashMap<String, CachedLookupResult> cache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, RateWindow> rateWindows = new ConcurrentHashMap<>();

    public PaymentCardBinLookupService(PaymentCardBinLookupProperties properties) {
        this.properties = properties;
    }

    public BinLookupResponse lookup(String prefix, String clientKey) {
        String normalizedPrefix = normalizePrefix(prefix);
        enforceRateLimit(clientKey);

        CachedLookupResult cached = cache.get(normalizedPrefix);
        if (cached != null && cached.expiresAt().isAfter(Instant.now())) {
            return cached.response();
        }

        BinLookupResponse response = lookupLocalCatalog(normalizedPrefix);
        long ttlSeconds = Math.max(30, properties.getCacheTtlSeconds());
        cache.put(normalizedPrefix, new CachedLookupResult(response, Instant.now().plusSeconds(ttlSeconds)));
        return response;
    }

    private BinLookupResponse lookupLocalCatalog(String prefix) {
        for (LocalBinEntry entry : LOCAL_BIN_CATALOG) {
            if (prefix.startsWith(entry.prefix())) {
                return matched(entry);
            }
        }
        return unknown();
    }

    private BinLookupResponse matched(LocalBinEntry entry) {
        BinLookupResponse response = new BinLookupResponse();
        response.setBrand(entry.brand());
        response.setIssuerBank(entry.issuerBank());
        response.setCardType(entry.cardType());
        response.setCountryCode(entry.countryCode());
        response.setStatus("MATCHED");
        return response;
    }

    private void enforceRateLimit(String clientKey) {
        String key = safeTrim(clientKey);
        if (key.isEmpty()) {
            key = "anonymous";
        }
        int maxPerMinute = Math.max(1, properties.getRateLimitPerMinute());
        long now = System.currentTimeMillis();

        RateWindow window = rateWindows.computeIfAbsent(key, ignored -> new RateWindow(now, 0));
        synchronized (window) {
            if (now - window.windowStartMs >= 60_000L) {
                window.windowStartMs = now;
                window.count = 0;
            }
            if (window.count >= maxPerMinute) {
                throw new BusinessException(ErrorMessages.BIN_LOOKUP_RATE_LIMITED, HttpStatus.TOO_MANY_REQUESTS);
            }
            window.count++;
        }
    }

    private BinLookupResponse unknown() {
        BinLookupResponse response = new BinLookupResponse();
        response.setBrand("UNKNOWN");
        response.setIssuerBank("");
        response.setCardType("");
        response.setCountryCode("");
        response.setStatus("UNKNOWN");
        return response;
    }

    private String normalizePrefix(String value) {
        return safeTrim(value).replaceAll("\\s+", "");
    }

    private String safeTrim(String value) {
        return Optional.ofNullable(value).orElse("").trim();
    }

    private record LocalBinEntry(String prefix, String brand, String issuerBank, String cardType, String countryCode) {
    }

    private record CachedLookupResult(BinLookupResponse response, Instant expiresAt) {
    }

    private static final class RateWindow {
        private long windowStartMs;
        private int count;

        private RateWindow(long windowStartMs, int count) {
            this.windowStartMs = windowStartMs;
            this.count = count;
        }
    }
}
