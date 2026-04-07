package com.group12.backend.service;

import java.util.List;

import com.group12.backend.dto.PaymentCardResponse;
import com.group12.backend.dto.StorePaymentCardRequest;

/**
 * TODO(ID2): 支付卡服务接口骨架。
 */
public interface PaymentCardService {
    PaymentCardResponse createCard(String userId, StorePaymentCardRequest request);
    List<PaymentCardResponse> listCards(String userId);
    void deleteCard(String userId, String cardId);
    PaymentCardResponse setDefaultCard(String userId, String cardId);
}
