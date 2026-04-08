package com.group12.backend.service.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Service;

import com.group12.backend.dto.SecuritySettingsRequest;
import com.group12.backend.dto.SecuritySettingsResponse;
import com.group12.backend.service.SecurityService;

/**
 * ID3: 账户安全增强实现。
 */
@Service
public class SecurityServiceImpl implements SecurityService {
    private static final boolean DEFAULT_TWO_FACTOR = false;
    private static final int DEFAULT_MAX_SESSION_COUNT = 3;
    private static final int DEFAULT_LOGIN_LOCK_MINUTES = 15;

    private final ConcurrentMap<Long, SecuritySettingsResponse> settingsStore = new ConcurrentHashMap<>();

    @Override
    public SecuritySettingsResponse getSecuritySettings(Long userId) {
        Long key = normalizeUserId(userId);
        SecuritySettingsResponse current = settingsStore.computeIfAbsent(key, ignored -> defaultSettings());
        return copyOf(current);
    }

    @Override
    public SecuritySettingsResponse updateSecuritySettings(Long userId, SecuritySettingsRequest request) {
        Long key = normalizeUserId(userId);
        SecuritySettingsResponse updated = settingsStore.compute(key, (ignored, existing) -> {
            SecuritySettingsResponse target = existing == null ? defaultSettings() : copyOf(existing);
            if (request != null) {
                if (request.getTwoFactorEnabled() != null) {
                    target.setTwoFactorEnabled(request.getTwoFactorEnabled());
                }
                if (request.getMaxSessionCount() != null) {
                    target.setMaxSessionCount(request.getMaxSessionCount());
                }
                if (request.getLoginLockMinutes() != null) {
                    target.setLoginLockMinutes(request.getLoginLockMinutes());
                }
            }
            return target;
        });
        return copyOf(updated);
    }

    private Long normalizeUserId(Long userId) {
        return userId == null || userId <= 0 ? 0L : userId;
    }

    private SecuritySettingsResponse defaultSettings() {
        SecuritySettingsResponse response = new SecuritySettingsResponse();
        response.setTwoFactorEnabled(DEFAULT_TWO_FACTOR);
        response.setMaxSessionCount(DEFAULT_MAX_SESSION_COUNT);
        response.setLoginLockMinutes(DEFAULT_LOGIN_LOCK_MINUTES);
        return response;
    }

    private SecuritySettingsResponse copyOf(SecuritySettingsResponse source) {
        SecuritySettingsResponse response = new SecuritySettingsResponse();
        response.setTwoFactorEnabled(source.getTwoFactorEnabled());
        response.setMaxSessionCount(source.getMaxSessionCount());
        response.setLoginLockMinutes(source.getLoginLockMinutes());
        return response;
    }
}
