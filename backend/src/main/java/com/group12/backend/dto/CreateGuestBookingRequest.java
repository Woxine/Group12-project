package com.group12.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * TODO(ID9): 未注册用户预订请求骨架（店员代下单）。
 */
public class CreateGuestBookingRequest {

    @NotBlank(message = "salespersonId is required")
    private String salespersonId;

    @NotBlank(message = "guestId is required")
    @Pattern(regexp = "^G?\\d+$", message = "guestId must be a numeric id, optionally prefixed with G")
    private String guestId;

    @NotBlank(message = "guestName is required")
    @Size(min = 2, max = 50, message = "guestName length must be between 2 and 50")
    private String guestName;

    @NotBlank(message = "guestContact is required")
    private String guestContact;

    @NotBlank(message = "scooterId is required")
    private String scooterId;

    @NotBlank(message = "duration is required")
    @Pattern(regexp = "^(10M|1H|4H|1D|1W)$", message = "duration must be one of: 10M, 1H, 4H, 1D, 1W")
    private String duration;

    public String getSalespersonId() {
        return salespersonId;
    }

    public void setSalespersonId(String salespersonId) {
        this.salespersonId = salespersonId;
    }

    public String getGuestId() {
        return guestId;
    }

    public void setGuestId(String guestId) {
        this.guestId = guestId;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getGuestContact() {
        return guestContact;
    }

    public void setGuestContact(String guestContact) {
        this.guestContact = guestContact;
    }

    public String getScooterId() {
        return scooterId;
    }

    public void setScooterId(String scooterId) {
        this.scooterId = scooterId;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
