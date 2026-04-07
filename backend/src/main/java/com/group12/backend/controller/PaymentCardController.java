package com.group12.backend.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group12.backend.dto.StorePaymentCardRequest;
import com.group12.backend.service.PaymentCardService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * TODO(ID2): 支付卡控制器骨架（仅 TODO，不含业务实现）。
 */
@RestController
@RequestMapping("/api/v1/users/{userId}/payment-cards")
public class PaymentCardController {
    private final PaymentCardService paymentCardService;

    public PaymentCardController(PaymentCardService paymentCardService) {
        this.paymentCardService = paymentCardService;
    }

    @PostMapping
    public ResponseEntity<Object> createCard(@PathVariable String userId,
                                             @Valid @RequestBody StorePaymentCardRequest request,
                                             HttpServletRequest httpRequest) {
        // TODO: 校验 path userId 与登录用户一致
        // TODO: return Map.of("data", paymentCardService.createCard(userId, request))
        throw new UnsupportedOperationException("TODO: implement createCard endpoint");
    }

    @GetMapping
    public ResponseEntity<Object> listCards(@PathVariable String userId,
                                            HttpServletRequest httpRequest) {
        // TODO: 权限校验 + 查询列表
        throw new UnsupportedOperationException("TODO: implement listCards endpoint");
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Object> deleteCard(@PathVariable String userId,
                                             @PathVariable String cardId,
                                             HttpServletRequest httpRequest) {
        // TODO: 权限校验 + 删除卡片
        return ResponseEntity.ok(Map.of("data", Map.of("todo", "deleteCard")));
    }

    @PostMapping("/{cardId}/default")
    public ResponseEntity<Object> setDefaultCard(@PathVariable String userId,
                                                 @PathVariable String cardId,
                                                 HttpServletRequest httpRequest) {
        // TODO: 权限校验 + 设置默认卡
        throw new UnsupportedOperationException("TODO: implement setDefaultCard endpoint");
    }
}
