package com.group12.backend.dto;

import java.math.BigDecimal;

/**
 * 用于向前端返回滑板车列表展示所需的状态、位置和计费信息。
 */
public class ScooterResponse {
    private Long id;
    private String status;
    private Double locationLat;
    private Double locationLng;
    private BigDecimal hourRate;
    private String locationName; // Flattened from LocationPoint

    public ScooterResponse(Long id, String status, Double locationLat, Double locationLng, BigDecimal hourRate, String locationName) {
        this.id = id;
        this.status = status;
        this.locationLat = locationLat;
        this.locationLng = locationLng;
        this.hourRate = hourRate;
        this.locationName = locationName;
    }

    // Getters
    public Long getId() { return id; }
    public String getStatus() { return status; }
    public Double getLocationLat() { return locationLat; }
    public Double getLocationLng() { return locationLng; }
    public BigDecimal getHourRate() { return hourRate; }
    public String getLocationName() { return locationName; }
}
