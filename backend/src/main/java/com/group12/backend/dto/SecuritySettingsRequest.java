package com.group12.backend.dto;

import jakarta.validation.constraints.Min;

/**
 * ID3: 账户安全设置更新请求。
 */
public class SecuritySettingsRequest {
    private Boolean twoFactorEnabled;

    @Min(value = 1, message = "maxSessionCount must be at least 1")
    private Integer maxSessionCount;

    @Min(value = 0, message = "loginLockMinutes must be at least 0")
    private Integer loginLockMinutes;

    public Boolean getTwoFactorEnabled() { return twoFactorEnabled; }
    public void setTwoFactorEnabled(Boolean twoFactorEnabled) { this.twoFactorEnabled = twoFactorEnabled; }
    public Integer getMaxSessionCount() { return maxSessionCount; }
    public void setMaxSessionCount(Integer maxSessionCount) { this.maxSessionCount = maxSessionCount; }
    public Integer getLoginLockMinutes() { return loginLockMinutes; }
    public void setLoginLockMinutes(Integer loginLockMinutes) { this.loginLockMinutes = loginLockMinutes; }
}
