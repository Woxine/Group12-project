package com.group12.backend.dto;

import jakarta.validation.constraints.Pattern;

/**
 * TODO(ID10&11): 延长预订请求骨架。
 */
public class ExtendBookingRequest {
    @Pattern(regexp = "^(10M|1H|4H|1D|1W)?$", message = "Duration must be one of: 10M, 1H, 4H, 1D, 1W")
    private String duration;

    private Integer durationMinutes;

    @Pattern(regexp = "^(10M|1H|4H|1D|1W)?$", message = "durationCode must be one of: 10M, 1H, 4H, 1D, 1W")
    private String durationCode;

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    public String getDurationCode() { return durationCode; }
    public void setDurationCode(String durationCode) { this.durationCode = durationCode; }
}
