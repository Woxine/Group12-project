package com.group12.backend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * TODO(ID2): 支付卡保存请求体骨架。
 */
public class StorePaymentCardRequest {
    @NotBlank(message = "holderName is required")
    private String holderName;

    @NotBlank(message = "cardNumber is required")
    private String cardNumber;

    private String brand;
    private Integer expiryMonth;
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
