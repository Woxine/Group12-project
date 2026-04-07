package com.group12.backend.service.impl;

import org.springframework.stereotype.Service;

import com.group12.backend.dto.SecuritySettingsRequest;
import com.group12.backend.dto.SecuritySettingsResponse;
import com.group12.backend.service.SecurityService;

/**
 * TODO(ID3): 账户安全增强实现骨架（仅 TODO，不含业务实现）。
 */
@Service
public class SecurityServiceImpl implements SecurityService {
    @Override
    public SecuritySettingsResponse getSecuritySettings(Long userId) {
        // TODO: 查询用户安全配置
        throw new UnsupportedOperationException("TODO: implement getSecuritySettings");
    }

    @Override
    public SecuritySettingsResponse updateSecuritySettings(Long userId, SecuritySettingsRequest request) {
        // TODO: 更新用户安全配置，记录审计
        throw new UnsupportedOperationException("TODO: implement updateSecuritySettings");
    }
}
