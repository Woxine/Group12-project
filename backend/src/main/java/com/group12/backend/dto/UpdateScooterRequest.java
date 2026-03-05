package com.group12.backend.dto;

import java.math.BigDecimal;

public class UpdateScooterRequest {

    private String status;
    private Double location_lat;
    private Double location_lng;
    private BigDecimal hour_rate;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getLocation_lat() {
        return location_lat;
    }

    public void setLocation_lat(Double location_lat) {
        this.location_lat = location_lat;
    }

    public Double getLocation_lng() {
        return location_lng;
    }

    public void setLocation_lng(Double location_lng) {
        this.location_lng = location_lng;
    }

    public BigDecimal getHour_rate() {
        return hour_rate;
    }

    public void setHour_rate(BigDecimal hour_rate) {
        this.hour_rate = hour_rate;
    }
}

