package com.group12.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RejectDiscountVerificationRequest {
    @NotBlank(message = "Reject reason is required")
    @Size(max = 500, message = "Reject reason must not exceed 500 characters")
    private String reason;

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
