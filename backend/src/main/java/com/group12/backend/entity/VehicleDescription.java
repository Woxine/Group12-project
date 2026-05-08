package com.group12.backend.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "vehicle_descriptions")
public class VehicleDescription implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_type", nullable = false, unique = true, length = 20)
    private String vehicleType;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(name = "subtitle", nullable = false, length = 100)
    private String subtitle = "";

    @Column(name = "description", nullable = false, length = 500)
    private String description = "";

    @Column(name = "range_text", nullable = false, length = 100)
    private String rangeText = "";

    @Column(name = "speed_text", nullable = false, length = 100)
    private String speedText = "";

    @Column(name = "motor_text", nullable = false, length = 100)
    private String motorText = "";

    @Column(name = "advice", nullable = false, length = 500)
    private String advice = "";

    public VehicleDescription() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRangeText() { return rangeText; }
    public void setRangeText(String rangeText) { this.rangeText = rangeText; }

    public String getSpeedText() { return speedText; }
    public void setSpeedText(String speedText) { this.speedText = speedText; }

    public String getMotorText() { return motorText; }
    public void setMotorText(String motorText) { this.motorText = motorText; }

    public String getAdvice() { return advice; }
    public void setAdvice(String advice) { this.advice = advice; }
}
