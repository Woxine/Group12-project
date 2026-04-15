package com.group12.backend.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

/**
 * Payload for admin creation of a new scooter row.
 */
public class CreateScooterRequest {

    private String status;

    @NotNull
    private BigDecimal hour_rate;

    private Double location_lat;
    private Double location_lng;
    private String location_name;
    private Long location_point_id;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getHour_rate() {
        return hour_rate;
    }

    public void setHour_rate(BigDecimal hour_rate) {
        this.hour_rate = hour_rate;
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

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public Long getLocation_point_id() {
        return location_point_id;
    }

    public void setLocation_point_id(Long location_point_id) {
        this.location_point_id = location_point_id;
    }
}
