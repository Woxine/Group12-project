package com.group12.backend.service.impl;

import java.net.URI;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.group12.backend.config.PaymentCardBinLookupProperties;
import com.group12.backend.dto.BinLookupResponse;
import com.group12.backend.exception.BusinessException;
import com.group12.backend.exception.ErrorMessages;

@Service
public class PaymentCardBinLookupService {
    private static final Logger log = LoggerFactory.getLogger(PaymentCardBinLookupService.class);

    private final PaymentCardBinLookupProperties properties;
    private final ConcurrentHashMap<String, CachedLookupResult> cache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, RateWindow> rateWindows = new ConcurrentHashMap<>();

    public PaymentCardBinLookupService(PaymentCardBinLookupProperties properties) {
        this.properties = properties;
    }

    public BinLookupResponse lookup(String prefix, String clientKey) {
        enforceRateLimit(clientKey);

        CachedLookupResult cached = cache.get(prefix);
        if (cached != null && cached.expiresAt().isAfter(Instant.now())) {
            return cached.response();
        }

        BinLookupResponse response = fetchFromProvider(prefix);
        long ttlSeconds = Math.max(30, properties.getCacheTtlSeconds());
        cache.put(prefix, new CachedLookupResult(response, Instant.now().plusSeconds(ttlSeconds)));
        return response;
    }

    private BinLookupResponse fetchFromProvider(String prefix) {
        if (!properties.isEnabled()) {
            return degraded();
        }
        String endpoint = safeTrim(properties.getEndpoint());
        if (endpoint.isEmpty()) {
            return degraded();
        }
        String apiKey = safeTrim(properties.getApiKey());
        if (apiKey.isEmpty()) {
            return degraded();
        }

        try {
            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
            requestFactory.setConnectTimeout(Math.max(200, properties.getConnectTimeoutMs()));
            requestFactory.setReadTimeout(Math.max(200, properties.getReadTimeoutMs()));
            RestTemplate restTemplate = new RestTemplate(requestFactory);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String headerName = safeTrim(properties.getApiKeyHeader());
            if (!headerName.isEmpty()) {
                String keyPrefix = properties.getApiKeyPrefix() == null ? "" : properties.getApiKeyPrefix();
                headers.set(headerName, keyPrefix + apiKey);
            }

            URI uri = endpoint.contains("{prefix}")
                    ? URI.create(endpoint.replace("{prefix}", prefix))
                    : URI.create(endpoint + (endpoint.endsWith("/") ? "" : "/") + prefix);

            ResponseEntity<Map<String, Object>> providerResponse = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    });
            Map<?, ?> responseBody = providerResponse.getBody();
            return toLookupResponse(responseBody);
        } catch (Exception ex) {
            log.warn("BIN lookup provider unavailable, degraded mode enabled");
            return degraded();
        }
    }

    private BinLookupResponse toLookupResponse(Map<?, ?> raw) {
        if (raw == null || raw.isEmpty()) {
            return unknown();
        }

        String brand = normalize(safeString(raw, "brand", "scheme", "network"));
        String issuer = normalize(
                firstNonEmpty(
                        safeString(raw, "issuerBank", "issuer_name", "bank", "bankName"),
                        nestedString(raw, "issuer", "name"),
                        nestedString(raw, "bank", "name")));
        String cardType = normalize(safeString(raw, "cardType", "type", "funding"));
        String countryCode = normalize(
                firstNonEmpty(
                        safeString(raw, "countryCode", "country_code"),
                        nestedString(raw, "country", "alpha2"),
                        nestedString(raw, "country", "code")));

        if (brand.isEmpty() && issuer.isEmpty() && cardType.isEmpty() && countryCode.isEmpty()) {
            return unknown();
        }

        BinLookupResponse response = new BinLookupResponse();
        response.setBrand(brand.isEmpty() ? "UNKNOWN" : brand);
        response.setIssuerBank(issuer);
        response.setCardType(cardType);
        response.setCountryCode(countryCode);
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

    private String safeString(Map<?, ?> raw, String... keys) {
        for (String key : keys) {
            Object value = raw.get(key);
            if (value instanceof String str && !str.trim().isEmpty()) {
                return str.trim();
            }
        }
        return "";
    }

    private String nestedString(Map<?, ?> raw, String parent, String child) {
        Object value = raw.get(parent);
        if (!(value instanceof Map<?, ?> nested)) {
            return "";
        }
        Object childValue = nested.get(child);
        if (childValue instanceof String str && !str.trim().isEmpty()) {
            return str.trim();
        }
        return "";
    }

    private BinLookupResponse degraded() {
        BinLookupResponse response = new BinLookupResponse();
        response.setBrand("UNKNOWN");
        response.setIssuerBank("");
        response.setCardType("");
        response.setCountryCode("");
        response.setStatus("DEGRADED");
        return response;
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

    private String normalize(String value) {
        return safeTrim(value).toUpperCase();
    }

    private String firstNonEmpty(String... values) {
        for (String value : values) {
            String normalized = safeTrim(value);
            if (!normalized.isEmpty()) {
                return normalized;
            }
        }
        return "";
    }

    private String safeTrim(String value) {
        return Optional.ofNullable(value).orElse("").trim();
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
