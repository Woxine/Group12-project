package com.group12.backend.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.group12.backend.dto.PaymentCardResponse;
import com.group12.backend.dto.StorePaymentCardRequest;
import com.group12.backend.service.PaymentCardService;

/**
 * TODO(ID2): 支付卡服务实现骨架（仅 TODO，不含业务实现）。
 */
@Service
public class PaymentCardServiceImpl implements PaymentCardService {
    @Override
    public PaymentCardResponse createCard(String userId, StorePaymentCardRequest request) {
        // TODO: 校验用户归属、去重、默认卡策略
        throw new UnsupportedOperationException("TODO: implement createCard");
    }

    @Override
    public List<PaymentCardResponse> listCards(String userId) {
        // TODO: 查询卡片列表（默认卡优先）
        throw new UnsupportedOperationException("TODO: implement listCards");
    }

    @Override
    public void deleteCard(String userId, String cardId) {
        // TODO: 校验归属并删除，处理默认卡回填
        throw new UnsupportedOperationException("TODO: implement deleteCard");
    }

    @Override
    public PaymentCardResponse setDefaultCard(String userId, String cardId) {
        // TODO: 事务内切换默认卡
        throw new UnsupportedOperationException("TODO: implement setDefaultCard");
    }
}
