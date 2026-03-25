package com.group12.backend.exception;

public final class ErrorMessages {

    public static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
    public static final String BUSINESS_ERROR = "Business Error";
    public static final String VALIDATION_ERROR = "Validation Error";
    public static final String UNAUTHORIZED = "Unauthorized";

    public static final String UNEXPECTED_ERROR_MESSAGE = "An unexpected error occurred. Please contact support.";
    public static final String ACCESS_DENIED_MESSAGE = "Access denied. Invalid or missing token.";

    public static final String INVALID_EMAIL_OR_PASSWORD = "Invalid email or password";
    public static final String EMAIL_ALREADY_REGISTERED = "Email already registered";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String BOOKING_NOT_FOUND = "Booking not found";
    public static final String SCOOTER_NOT_FOUND = "Scooter not found";
    public static final String OVERLAPPING_BOOKING = "Scooter has overlapping reservation. Please try another scooter or time.";
    public static final String ACTIVE_BOOKING_EXISTS = "You already have an active booking. Please cancel it or wait until it ends before booking again.";

    private ErrorMessages() {
    }

    public static String scooterUnavailable(String status) {
        return "Scooter is not available (Current status: " + status + ")";
    }

    public static String cannotCancelBooking(String status) {
        return "Cannot cancel booking with status: " + status;
    }

    public static String cannotCompleteBooking(String status) {
        return "Cannot complete booking with status: " + status;
    }

    public static String feedbackNotFound(String feedbackId) {
        return "Feedback not found with id: " + feedbackId;
    }
}
