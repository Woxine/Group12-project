package com.group12.backend.sprint2.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.group12.backend.dto.SecuritySettingsRequest;
import com.group12.backend.dto.SecuritySettingsResponse;
import com.group12.backend.service.impl.SecurityServiceImpl;

/**
 * ID3 安全设置服务：断言实现完成后的契约；骨架阶段失败直至 {@link SecurityServiceImpl} 落地。
 */
@DisplayName("ID3 SecurityService")
class SecurityServiceTest {

    private SecurityServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new SecurityServiceImpl();
    }

    @Test
    @DisplayName("getSecuritySettings：返回非空配置")
    void getSecuritySettings_returnsNonNull() {
        assertThatCode(() -> service.getSecuritySettings(1L)).doesNotThrowAnyException();
        SecuritySettingsResponse r = service.getSecuritySettings(1L);
        assertThat(r).isNotNull();
    }

    @Test
    @DisplayName("updateSecuritySettings：返回更新后的非空配置")
    void updateSecuritySettings_returnsNonNull() {
        SecuritySettingsRequest req = new SecuritySettingsRequest();
        req.setTwoFactorEnabled(false);
        assertThatCode(() -> service.updateSecuritySettings(1L, req)).doesNotThrowAnyException();
        SecuritySettingsResponse r = service.updateSecuritySettings(1L, req);
        assertThat(r).isNotNull();
    }
}
