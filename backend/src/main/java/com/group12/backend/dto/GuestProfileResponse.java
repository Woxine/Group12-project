package com.group12.backend.dto;

/**
 * TODO(ID9): 未注册用户档案返回体骨架。
 */
public class GuestProfileResponse {

    private String guestId;
    private String displayName;
    private String maskedContact;
    private Integer bookingCount;

    public String getGuestId() {
        return guestId;
    }

    public void setGuestId(String guestId) {
        this.guestId = guestId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getMaskedContact() {
        return maskedContact;
    }

    public void setMaskedContact(String maskedContact) {
        this.maskedContact = maskedContact;
    }

    public Integer getBookingCount() {
        return bookingCount;
    }

    public void setBookingCount(Integer bookingCount) {
        this.bookingCount = bookingCount;
    }

    public static GuestProfileResponse fromGuestEntity(Object guestEntity, Integer totalBookings) {
        // TODO(ID9): Map guest entity fields to response DTO.
        GuestProfileResponse response = new GuestProfileResponse();
        response.setBookingCount(totalBookings);
        return response;
    }

    public static String maskContact(String contact) {
        // TODO(ID9): Implement mask strategy for phone/email.
        return contact;
    }
}
