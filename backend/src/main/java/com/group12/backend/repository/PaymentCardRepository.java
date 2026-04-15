package com.group12.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group12.backend.entity.PaymentCard;

@Repository
public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long> {
    List<PaymentCard> findByUser_IdOrderByCreatedAtDesc(Long userId);

    boolean existsByUser_Id(Long userId);

    Optional<PaymentCard> findByIdAndUser_Id(Long cardId, Long userId);

    Optional<PaymentCard> findByUser_IdAndIsDefaultTrue(Long userId);

    boolean existsByUser_IdAndBrandIgnoreCaseAndLast4AndExpiryMonthAndExpiryYear(
            Long userId,
            String brand,
            String last4,
            Integer expiryMonth,
            Integer expiryYear);
}
