package com.group12.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * TODO(ID10&11): 延长预订请求骨架。
 */
public class ExtendBookingRequest {
    @NotBlank(message = "duration is required")
    @Pattern(regexp = "^(10M|1H|4H|1D|1W)$", message = "Duration must be one of: 10M, 1H, 4H, 1D, 1W")
    private String duration;

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
}
