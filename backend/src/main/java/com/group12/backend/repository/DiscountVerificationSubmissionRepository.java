package com.group12.backend.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group12.backend.entity.DiscountVerificationSubmission;

@Repository
public interface DiscountVerificationSubmissionRepository extends JpaRepository<DiscountVerificationSubmission, Long> {
    Optional<DiscountVerificationSubmission> findTopByUser_IdAndTypeOrderByVersionDesc(Long userId, String type);

    Optional<DiscountVerificationSubmission> findTopByUser_IdAndTypeAndStatusOrderByVersionDesc(Long userId, String type, String status);

    Page<DiscountVerificationSubmission> findByUser_IdOrderBySubmittedAtDesc(Long userId, Pageable pageable);

    Page<DiscountVerificationSubmission> findByStatusOrderBySubmittedAtDesc(String status, Pageable pageable);

    Page<DiscountVerificationSubmission> findByTypeOrderBySubmittedAtDesc(String type, Pageable pageable);

    Page<DiscountVerificationSubmission> findByTypeAndStatusOrderBySubmittedAtDesc(String type, String status, Pageable pageable);
}
