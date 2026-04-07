package com.group12.backend.dto;

/**
 * TODO(ID7): 预订确认邮件载荷骨架。
 */
public class BookingConfirmationEmailPayload {
    private String email;
    private String bookingId;
    private String scooterId;
    private String duration;
    private String totalPrice;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public String getScooterId() { return scooterId; }
    public void setScooterId(String scooterId) { this.scooterId = scooterId; }
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    public String getTotalPrice() { return totalPrice; }
    public void setTotalPrice(String totalPrice) { this.totalPrice = totalPrice; }
}
