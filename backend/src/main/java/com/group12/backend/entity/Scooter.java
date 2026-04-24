package com.group12.backend.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "scooters")
public class Scooter implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String status; // AVAILABLE, RESERVED, RENTED, MAINTENANCE

    @Column(nullable = false)
    private String type = "GEN1";

    @Column(name = "location_lat")
    private Double locationLat;

    @Column(name = "location_lng")
    private Double locationLng;

    @Column(name = "hour_rate", nullable = false)
    private BigDecimal hourRate;

    @ManyToOne
    @JoinColumn(name = "location_point_id")
    private LocationPoint locationPoint;

    /**
     * When false, the scooter is omitted from public listings and cannot be booked (soft-remove from fleet).
     */
    @Column(nullable = false)
    private Boolean visible = true;

    public Scooter() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Double getLocationLat() { return locationLat; }
    public void setLocationLat(Double locationLat) { this.locationLat = locationLat; }

    public Double getLocationLng() { return locationLng; }
    public void setLocationLng(Double locationLng) { this.locationLng = locationLng; }

    public BigDecimal getHourRate() { return hourRate; }
    public void setHourRate(BigDecimal hourRate) { this.hourRate = hourRate; }

    public LocationPoint getLocationPoint() { return locationPoint; }
    public void setLocationPoint(LocationPoint locationPoint) { this.locationPoint = locationPoint; }

    public Boolean getVisible() { return visible; }
    public void setVisible(Boolean visible) { this.visible = visible; }
}
