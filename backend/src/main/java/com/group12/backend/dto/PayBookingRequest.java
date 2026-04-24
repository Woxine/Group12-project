package com.group12.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class PayBookingRequest {
    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    private String paymentCardId;

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentCardId() {
        return paymentCardId;
    }

    public void setPaymentCardId(String paymentCardId) {
        this.paymentCardId = paymentCardId;
    }
}
