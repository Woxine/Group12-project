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

    /**
     * TODO(ID9): 店员为未注册用户绑定信用卡。
     */
    default PaymentCardResponse bindCardForGuest(String guestId, StorePaymentCardRequest request, String salespersonId) {
        throw new UnsupportedOperationException("TODO(ID9): bindCardForGuest not implemented yet");
    }

    /**
     * ID9: 店员按 guestId 查询顾客卡片列表。
     */
    default List<PaymentCardResponse> listCardsForGuest(String guestId, String salespersonId) {
        throw new UnsupportedOperationException("TODO(ID9): listCardsForGuest not implemented yet");
    }
}
