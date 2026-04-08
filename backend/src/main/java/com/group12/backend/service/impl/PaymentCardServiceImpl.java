package com.group12.backend.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.group12.backend.dto.PaymentCardResponse;
import com.group12.backend.dto.StorePaymentCardRequest;
import com.group12.backend.entity.PaymentCard;
import com.group12.backend.entity.User;
import com.group12.backend.exception.BusinessException;
import com.group12.backend.exception.ErrorMessages;
import com.group12.backend.repository.PaymentCardRepository;
import com.group12.backend.repository.UserRepository;
import com.group12.backend.service.PaymentCardService;

/**
 * TODO(ID2): 支付卡服务实现骨架（仅 TODO，不含业务实现）。
 */
@Service
public class PaymentCardServiceImpl implements PaymentCardService {
    private static final DateTimeFormatter CREATED_AT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final PaymentCardRepository paymentCardRepository;
    private final UserRepository userRepository;

    public PaymentCardServiceImpl(PaymentCardRepository paymentCardRepository, UserRepository userRepository) {
        this.paymentCardRepository = paymentCardRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public PaymentCardResponse createCard(String userId, StorePaymentCardRequest request) {
        Long parsedUserId = parseId(userId, "userId");
        User user = userRepository.findById(parsedUserId)
                .orElseThrow(() -> new BusinessException(ErrorMessages.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        String cardNumber = normalizeCardNumber(request.getCardNumber());
        validateCardNumber(cardNumber);
        validateExpiry(request.getExpiryMonth(), request.getExpiryYear());

        String last4 = cardNumber.substring(cardNumber.length() - 4);
        String normalizedBrand = normalizeBrand(request.getBrand());

        boolean duplicate = paymentCardRepository.existsByUser_IdAndBrandIgnoreCaseAndLast4AndExpiryMonthAndExpiryYear(
                parsedUserId,
                normalizedBrand,
                last4,
                request.getExpiryMonth(),
                request.getExpiryYear());
        if (duplicate) {
            throw new BusinessException(ErrorMessages.PAYMENT_CARD_DUPLICATE, HttpStatus.CONFLICT);
        }

        PaymentCard card = new PaymentCard();
        card.setUser(user);
        card.setHolderName(request.getHolderName().trim());
        card.setBrand(normalizedBrand);
        card.setLast4(last4);
        card.setExpiryMonth(request.getExpiryMonth());
        card.setExpiryYear(request.getExpiryYear());
        card.setIsDefault(paymentCardRepository.findByUser_IdAndIsDefaultTrue(parsedUserId).isEmpty());

        return toResponse(paymentCardRepository.save(card));
    }

    @Override
    public List<PaymentCardResponse> listCards(String userId) {
        Long parsedUserId = parseId(userId, "userId");
        ensureUserExists(parsedUserId);
        List<PaymentCard> cards = paymentCardRepository.findByUser_IdOrderByCreatedAtDesc(parsedUserId);
        return cards.stream()
                .sorted(Comparator
                        .comparing((PaymentCard card) -> !Boolean.TRUE.equals(card.getIsDefault()))
                        .thenComparing(PaymentCard::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCard(String userId, String cardId) {
        Long parsedUserId = parseId(userId, "userId");
        Long parsedCardId = parseId(cardId, "cardId");
        ensureUserExists(parsedUserId);

        PaymentCard target = paymentCardRepository.findByIdAndUser_Id(parsedCardId, parsedUserId)
                .orElseThrow(() -> new BusinessException(ErrorMessages.PAYMENT_CARD_NOT_FOUND, HttpStatus.NOT_FOUND));
        boolean wasDefault = Boolean.TRUE.equals(target.getIsDefault());
        paymentCardRepository.delete(target);

        if (wasDefault) {
            List<PaymentCard> remainingCards = paymentCardRepository.findByUser_IdOrderByCreatedAtDesc(parsedUserId);
            if (!remainingCards.isEmpty()) {
                PaymentCard newDefaultCard = remainingCards.get(0);
                if (!Boolean.TRUE.equals(newDefaultCard.getIsDefault())) {
                    newDefaultCard.setIsDefault(true);
                    paymentCardRepository.save(newDefaultCard);
                }
            }
        }
    }

    @Override
    @Transactional
    public PaymentCardResponse setDefaultCard(String userId, String cardId) {
        Long parsedUserId = parseId(userId, "userId");
        Long parsedCardId = parseId(cardId, "cardId");
        ensureUserExists(parsedUserId);

        PaymentCard target = paymentCardRepository.findByIdAndUser_Id(parsedCardId, parsedUserId)
                .orElseThrow(() -> new BusinessException(ErrorMessages.PAYMENT_CARD_NOT_FOUND, HttpStatus.NOT_FOUND));
        if (Boolean.TRUE.equals(target.getIsDefault())) {
            return toResponse(target);
        }

        paymentCardRepository.findByUser_IdAndIsDefaultTrue(parsedUserId).ifPresent(oldDefault -> {
            oldDefault.setIsDefault(false);
            paymentCardRepository.save(oldDefault);
        });

        target.setIsDefault(true);
        return toResponse(paymentCardRepository.save(target));
    }

    private void ensureUserExists(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new BusinessException(ErrorMessages.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
    }

    private Long parseId(String raw, String fieldName) {
        try {
            return Long.parseLong(raw);
        } catch (Exception ex) {
            throw new BusinessException(fieldName + " is invalid", HttpStatus.BAD_REQUEST);
        }
    }

    private String normalizeCardNumber(String input) {
        if (input == null) {
            return "";
        }
        return input.replaceAll("\\s+", "").trim();
    }

    private void validateCardNumber(String cardNumber) {
        if (!cardNumber.matches("\\d{13,19}") || !isLuhnValid(cardNumber)) {
            throw new BusinessException(ErrorMessages.INVALID_CARD_NUMBER, HttpStatus.BAD_REQUEST);
        }
    }

    private void validateExpiry(Integer month, Integer year) {
        if (month == null || year == null) {
            throw new BusinessException(ErrorMessages.PAYMENT_CARD_EXPIRED, HttpStatus.BAD_REQUEST);
        }

        LocalDateTime now = LocalDateTime.now();
        if (year < now.getYear() || (year == now.getYear() && month < now.getMonthValue())) {
            throw new BusinessException(ErrorMessages.PAYMENT_CARD_EXPIRED, HttpStatus.BAD_REQUEST);
        }
    }

    private String normalizeBrand(String brand) {
        return brand == null ? "" : brand.trim();
    }

    private boolean isLuhnValid(String cardNumber) {
        int sum = 0;
        boolean shouldDouble = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = cardNumber.charAt(i) - '0';
            if (shouldDouble) {
                digit = digit * 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }
            sum += digit;
            shouldDouble = !shouldDouble;
        }
        return sum % 10 == 0;
    }

    private PaymentCardResponse toResponse(PaymentCard card) {
        PaymentCardResponse response = new PaymentCardResponse();
        response.setId(String.valueOf(card.getId()));
        response.setHolderName(card.getHolderName());
        response.setBrand(card.getBrand());
        response.setMaskedNumber("**** **** **** " + card.getLast4());
        response.setExpiryMonth(card.getExpiryMonth());
        response.setExpiryYear(card.getExpiryYear());
        response.setIsDefault(Boolean.TRUE.equals(card.getIsDefault()));
        response.setCreatedAt(card.getCreatedAt() == null ? null : card.getCreatedAt().format(CREATED_AT_FMT));
        return response;
    }
}
