package com.group12.backend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 用于接收用户创建滑板车预约订单时提交的请求参数。
 */
public class CreateBookingRequest {
    @NotBlank(message = "Scooter ID is required")
    private String scooter_id;

    @NotBlank(message = "User ID is required")
    private String user_id;

    @NotBlank(message = "Duration is required")
    @jakarta.validation.constraints.Pattern(regexp = "^(10M|1H|4H|1D|1W)$", message = "Duration must be one of: 10M, 1H, 4H, 1D, 1W")
    private String duration; // 10M, 1H, 4H, 1D, 1W

    public String getScooter_id() { return scooter_id; }
    public void setScooter_id(String scooter_id) { this.scooter_id = scooter_id; }
    
    public String getUser_id() { return user_id; }
    public void setUser_id(String user_id) { this.user_id = user_id; }
    
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    private Double startLat;
    private Double startLng;

    public Double getStartLat() { return startLat; }
    public void setStartLat(Double startLat) { this.startLat = startLat; }
    public Double getStartLng() { return startLng; }
    public void setStartLng(Double startLng) { this.startLng = startLng; }
}
