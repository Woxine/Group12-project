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
    public static final String FORBIDDEN = "Forbidden";
    public static final String PASSWORD_INCORRECT = "Current password is incorrect";
    public static final String SAME_PASSWORD_NOT_ALLOWED = "New password must be different from current password";
    public static final String SAME_EMAIL_NOT_ALLOWED = "New email must be different from current email";
    public static final String SAME_NAME_NOT_ALLOWED = "New username must be different from current username";
    public static final String USERNAME_ALREADY_TAKEN = "Username is already taken";
    public static final String BOOKING_NOT_FOUND = "Booking not found";
    public static final String SCOOTER_NOT_FOUND = "Scooter not found";
    public static final String OVERLAPPING_BOOKING = "Scooter has overlapping reservation. Please try another scooter or time.";
    public static final String ACTIVE_BOOKING_EXISTS = "You already have an active booking. Please cancel it or wait until it ends before booking again.";
    public static final String BOOKING_CONCURRENT_CONFLICT = "Booking conflict detected. Another client may have updated this booking.";
    public static final String PAYMENT_CARD_NOT_FOUND = "Payment card not found";
    public static final String PAYMENT_CARD_DUPLICATE = "Payment card already exists";
    public static final String PAYMENT_CARD_EXPIRED = "Payment card is expired";
    public static final String INVALID_CARD_NUMBER = "Invalid card number";
    public static final String PAYMENT_CARD_REQUIRED_FOR_BOOKING = "Please bind a payment card before booking.";
    public static final String BOOKING_PAYMENT_EXPIRED = "Payment window expired. Please place a new booking.";
    public static final String BOOKING_ALREADY_PAID = "Booking has already been paid.";
    public static final String BIN_LOOKUP_RATE_LIMITED = "Too many BIN lookup requests. Please try again later.";
    public static final String INVALID_FEEDBACK_ID = "Invalid feedback id";
    public static final String INVALID_FEEDBACK_PROCESS_ACTION = "Invalid process action";
    public static final String ESCALATE_TARGET_REQUIRED = "Escalate target is required for ESCALATE action";
    public static final String HIGH_PRIORITY_REQUIRES_ESCALATION = "HIGH priority feedback must use ESCALATE action";
    public static final String LOW_PRIORITY_DIRECT_HANDLE_ONLY = "LOW priority feedback must use DIRECT_HANDLE action";
    public static final String FEEDBACK_ALREADY_ESCALATED = "Feedback is already escalated";
    public static final String FEEDBACK_ALREADY_RESOLVED = "Feedback is already resolved";

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

    public static String bookingStateChanged(String status) {
        return "Booking state has changed to: " + status;
    }

    public static String cannotExtendBooking(String status) {
        return "Cannot extend booking with status: " + status;
    }

    public static String feedbackNotFound(String feedbackId) {
        return "Feedback not found with id: " + feedbackId;
    }
}
