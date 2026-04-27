package com.group12.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class BinLookupRequest {
    @NotBlank(message = "prefix is required")
    @Pattern(regexp = "^\\d{6,8}$", message = "prefix must be 6-8 digits")
    private String prefix;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
