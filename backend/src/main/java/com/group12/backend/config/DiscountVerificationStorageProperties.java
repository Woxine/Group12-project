package com.group12.backend.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "discount-verification.storage")
public class DiscountVerificationStorageProperties {
    private String rootDir = "uploads/discount-verifications";
    private long maxFileSizeBytes = 5 * 1024 * 1024;
    private List<String> allowedMimeTypes = new ArrayList<>(List.of("image/jpeg", "image/png", "application/pdf"));

    public String getRootDir() { return rootDir; }
    public void setRootDir(String rootDir) { this.rootDir = rootDir; }

    public long getMaxFileSizeBytes() { return maxFileSizeBytes; }
    public void setMaxFileSizeBytes(long maxFileSizeBytes) { this.maxFileSizeBytes = maxFileSizeBytes; }

    public List<String> getAllowedMimeTypes() { return allowedMimeTypes; }
    public void setAllowedMimeTypes(List<String> allowedMimeTypes) { this.allowedMimeTypes = allowedMimeTypes; }
}
