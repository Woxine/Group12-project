package com.group12.backend.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group12.backend.dto.StorePaymentCardRequest;
import com.group12.backend.exception.BusinessException;
import com.group12.backend.exception.ErrorMessages;
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
        ensureUserOwnership(userId, httpRequest);
        return ResponseEntity.ok(Map.of("data", paymentCardService.createCard(userId, request)));
    }

    @GetMapping
    public ResponseEntity<Object> listCards(@PathVariable String userId,
                                            HttpServletRequest httpRequest) {
        ensureUserOwnership(userId, httpRequest);
        return ResponseEntity.ok(Map.of("data", paymentCardService.listCards(userId)));
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Object> deleteCard(@PathVariable String userId,
                                             @PathVariable String cardId,
                                             HttpServletRequest httpRequest) {
        ensureUserOwnership(userId, httpRequest);
        paymentCardService.deleteCard(userId, cardId);
        return ResponseEntity.ok(Map.of("data", Map.of("message", "Payment card deleted")));
    }

    @PostMapping("/{cardId}/default")
    public ResponseEntity<Object> setDefaultCard(@PathVariable String userId,
                                                 @PathVariable String cardId,
                                                 HttpServletRequest httpRequest) {
        ensureUserOwnership(userId, httpRequest);
        return ResponseEntity.ok(Map.of("data", paymentCardService.setDefaultCard(userId, cardId)));
    }

    private void ensureUserOwnership(String userId, HttpServletRequest httpRequest) {
        Object authUserId = httpRequest.getAttribute("userId");
        if (authUserId == null || !userId.equals(String.valueOf(authUserId))) {
            throw new BusinessException(ErrorMessages.FORBIDDEN, HttpStatus.FORBIDDEN);
        }
    }
}
