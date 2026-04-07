package com.group12.backend.service;

import com.group12.backend.dto.SecuritySettingsRequest;
import com.group12.backend.dto.SecuritySettingsResponse;

/**
 * TODO(ID3): 账户安全增强服务骨架。
 */
public interface SecurityService {
    SecuritySettingsResponse getSecuritySettings(Long userId);
    SecuritySettingsResponse updateSecuritySettings(Long userId, SecuritySettingsRequest request);
}
