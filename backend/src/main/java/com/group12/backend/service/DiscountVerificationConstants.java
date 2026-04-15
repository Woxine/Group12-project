package com.group12.backend.service;

public final class DiscountVerificationConstants {
    public static final String TYPE_STUDENT = "STUDENT";
    public static final String TYPE_SENIOR = "SENIOR";

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_REJECTED = "REJECTED";

    private DiscountVerificationConstants() {
    }

    public static boolean isSupportedType(String type) {
        return TYPE_STUDENT.equalsIgnoreCase(type) || TYPE_SENIOR.equalsIgnoreCase(type);
    }
}
