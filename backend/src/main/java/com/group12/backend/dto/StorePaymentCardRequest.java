package com.group12.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * TODO(ID2): 支付卡保存请求体骨架。
 */
public class StorePaymentCardRequest {
    @NotBlank(message = "holderName is required")
    @Pattern(regexp = "^[A-Za-z ]{2,50}$", message = "holderName must contain only letters and spaces")
    private String holderName;

    @NotBlank(message = "cardNumber is required")
    @Pattern(regexp = "^\\d{13,19}$", message = "cardNumber must be 13-19 digits")
    private String cardNumber;

    @NotBlank(message = "brand is required")
    private String brand;

    @Min(value = 1, message = "expiryMonth must be between 1 and 12")
    @Max(value = 12, message = "expiryMonth must be between 1 and 12")
    private Integer expiryMonth;

    @Min(value = 2024, message = "expiryYear is invalid")
    @Max(value = 2100, message = "expiryYear is invalid")
    private Integer expiryYear;

    public String getHolderName() { return holderName; }
    public void setHolderName(String holderName) { this.holderName = holderName; }
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public Integer getExpiryMonth() { return expiryMonth; }
    public void setExpiryMonth(Integer expiryMonth) { this.expiryMonth = expiryMonth; }
    public Integer getExpiryYear() { return expiryYear; }
    public void setExpiryYear(Integer expiryYear) { this.expiryYear = expiryYear; }
}
