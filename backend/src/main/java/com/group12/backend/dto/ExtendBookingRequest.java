package com.group12.backend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * TODO(ID10&11): 延长预订请求骨架。
 */
public class ExtendBookingRequest {
    @NotBlank(message = "duration is required")
    private String duration;

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
}
