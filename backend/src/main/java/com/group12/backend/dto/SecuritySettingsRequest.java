package com.group12.backend.dto;

/**
 * TODO(ID3): 账户安全设置更新请求骨架。
 */
public class SecuritySettingsRequest {
    private Boolean twoFactorEnabled;
    private Integer maxSessionCount;
    private Integer loginLockMinutes;

    public Boolean getTwoFactorEnabled() { return twoFactorEnabled; }
    public void setTwoFactorEnabled(Boolean twoFactorEnabled) { this.twoFactorEnabled = twoFactorEnabled; }
    public Integer getMaxSessionCount() { return maxSessionCount; }
    public void setMaxSessionCount(Integer maxSessionCount) { this.maxSessionCount = maxSessionCount; }
    public Integer getLoginLockMinutes() { return loginLockMinutes; }
    public void setLoginLockMinutes(Integer loginLockMinutes) { this.loginLockMinutes = loginLockMinutes; }
}
