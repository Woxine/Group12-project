package com.group12.backend.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * Payload for batch creation of scooters. Each entry can specify a count
 * to create multiple identical scooters at once.
 */
public class BatchCreateScooterRequest {

    @NotEmpty
    @Valid
    private List<Item> scooters;

    public List<Item> getScooters() {
        return scooters;
    }

    public void setScooters(List<Item> scooters) {
        this.scooters = scooters;
    }

    public static class Item {

        private String type;
        private String status;

        @NotNull
        private BigDecimal hour_rate;

        @NotNull
        @Min(1)
        private Integer count = 1;

        private Double location_lat;
        private Double location_lng;
        private String location_name;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

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

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
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
    }
}
