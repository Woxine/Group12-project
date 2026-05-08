package com.group12.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class VehicleDescriptionRequest {

    @NotBlank(message = "Vehicle type is required")
    @Size(max = 20)
    private String vehicle_type;

    @Size(max = 100)
    private String display_name;

    @Size(max = 100)
    private String subtitle;

    @Size(max = 500)
    private String description;

    @Size(max = 100)
    private String range_text;

    @Size(max = 100)
    private String speed_text;

    @Size(max = 100)
    private String motor_text;

    @Size(max = 500)
    private String advice;

    public String getVehicle_type() { return vehicle_type; }
    public void setVehicle_type(String vehicle_type) { this.vehicle_type = vehicle_type; }

    public String getDisplay_name() { return display_name; }
    public void setDisplay_name(String display_name) { this.display_name = display_name; }

    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRange_text() { return range_text; }
    public void setRange_text(String range_text) { this.range_text = range_text; }

    public String getSpeed_text() { return speed_text; }
    public void setSpeed_text(String speed_text) { this.speed_text = speed_text; }

    public String getMotor_text() { return motor_text; }
    public void setMotor_text(String motor_text) { this.motor_text = motor_text; }

    public String getAdvice() { return advice; }
    public void setAdvice(String advice) { this.advice = advice; }
}
