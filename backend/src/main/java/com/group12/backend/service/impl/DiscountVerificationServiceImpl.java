package com.group12.backend.service.impl;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.group12.backend.dto.DiscountVerificationSubmissionResponse;
import com.group12.backend.entity.DiscountVerificationSubmission;
import com.group12.backend.entity.User;
import com.group12.backend.exception.BusinessException;
import com.group12.backend.exception.ErrorMessages;
import com.group12.backend.repository.DiscountVerificationSubmissionRepository;
import com.group12.backend.repository.UserRepository;
import com.group12.backend.service.DiscountDocumentStorage;
import com.group12.backend.service.DiscountVerificationConstants;
import com.group12.backend.service.DiscountVerificationService;

@Service
public class DiscountVerificationServiceImpl implements DiscountVerificationService {

    private final DiscountVerificationSubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final DiscountDocumentStorage discountDocumentStorage;

    public DiscountVerificationServiceImpl(
            DiscountVerificationSubmissionRepository submissionRepository,
            UserRepository userRepository,
            DiscountDocumentStorage discountDocumentStorage) {
        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
        this.discountDocumentStorage = discountDocumentStorage;
    }

    @Override
    @Transactional
    public Object submit(Long userId, String type, MultipartFile file) {
        String normalizedType = normalizeType(type);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorMessages.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        int nextVersion = submissionRepository.findTopByUser_IdAndTypeOrderByVersionDesc(userId, normalizedType)
                .map(submission -> submission.getVersion() + 1)
                .orElse(1);

        DiscountDocumentStorage.StoredDiscountDocument stored = discountDocumentStorage.store(
                userId, normalizedType, nextVersion, file
        );

        DiscountVerificationSubmission submission = new DiscountVerificationSubmission();
        submission.setUser(user);
        submission.setType(normalizedType);
        submission.setStatus(DiscountVerificationConstants.STATUS_PENDING);
        submission.setStoragePath(stored.storagePath());
        submission.setOriginalFilename(stored.originalFilename());
        submission.setMimeType(stored.mimeType());
        submission.setSizeBytes(stored.sizeBytes());
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setVersion(nextVersion);

        return toResponse(submissionRepository.save(submission));
    }

    @Override
    public Object getUserSubmissions(Long userId, Integer page, Integer size) {
        int pageNum = page != null && page > 0 ? page : 1;
        int pageSize = size != null && size > 0 ? size : 10;
        Page<DiscountVerificationSubmission> paged = submissionRepository.findByUser_IdOrderBySubmittedAtDesc(
                userId, PageRequest.of(pageNum - 1, pageSize)
        );
        return pageToMap(paged);
    }

    @Override
    public Object getAdminSubmissions(String status, String type, Integer page, Integer size) {
        int pageNum = page != null && page > 0 ? page : 1;
        int pageSize = size != null && size > 0 ? size : 10;
        PageRequest pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<DiscountVerificationSubmission> paged;

        String normalizedType = type == null || type.isBlank() ? null : normalizeType(type);
        String normalizedStatus = status == null || status.isBlank() ? null : normalizeStatus(status);

        if (normalizedType != null && normalizedStatus != null) {
            paged = submissionRepository.findByTypeAndStatusOrderBySubmittedAtDesc(normalizedType, normalizedStatus, pageable);
        } else if (normalizedType != null) {
            paged = submissionRepository.findByTypeOrderBySubmittedAtDesc(normalizedType, pageable);
        } else if (normalizedStatus != null) {
            paged = submissionRepository.findByStatusOrderBySubmittedAtDesc(normalizedStatus, pageable);
        } else {
            paged = submissionRepository.findAll(pageable);
        }

        return pageToMap(paged);
    }

    @Override
    @Transactional
    public Object approve(Long submissionId, Long reviewerUserId) {
        DiscountVerificationSubmission submission = findPendingSubmission(submissionId);
        submission.setStatus(DiscountVerificationConstants.STATUS_APPROVED);
        submission.setReviewedAt(LocalDateTime.now());
        submission.setReviewerUserId(reviewerUserId);
        submission.setRejectReason(null);
        return toResponse(submissionRepository.save(submission));
    }

    @Override
    @Transactional
    public Object reject(Long submissionId, Long reviewerUserId, String reason) {
        DiscountVerificationSubmission submission = findPendingSubmission(submissionId);
        submission.setStatus(DiscountVerificationConstants.STATUS_REJECTED);
        submission.setReviewedAt(LocalDateTime.now());
        submission.setReviewerUserId(reviewerUserId);
        submission.setRejectReason(reason == null ? "" : reason.trim());
        return toResponse(submissionRepository.save(submission));
    }

    private DiscountVerificationSubmission findPendingSubmission(Long submissionId) {
        DiscountVerificationSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new BusinessException("Verification submission not found", HttpStatus.NOT_FOUND));
        if (!DiscountVerificationConstants.STATUS_PENDING.equalsIgnoreCase(submission.getStatus())) {
            throw new BusinessException("Only pending submissions can be reviewed", HttpStatus.BAD_REQUEST);
        }
        return submission;
    }

    private static String normalizeType(String type) {
        if (!DiscountVerificationConstants.isSupportedType(type)) {
            throw new BusinessException("Verification type must be STUDENT or SENIOR", HttpStatus.BAD_REQUEST);
        }
        return type.trim().toUpperCase();
    }

    private static String normalizeStatus(String status) {
        String normalized = status.trim().toUpperCase();
        if (!DiscountVerificationConstants.STATUS_PENDING.equals(normalized)
                && !DiscountVerificationConstants.STATUS_APPROVED.equals(normalized)
                && !DiscountVerificationConstants.STATUS_REJECTED.equals(normalized)) {
            throw new BusinessException("Status must be PENDING, APPROVED or REJECTED", HttpStatus.BAD_REQUEST);
        }
        return normalized;
    }

    private Map<String, Object> pageToMap(Page<DiscountVerificationSubmission> page) {
        return Map.of(
                "data", page.getContent().stream().map(this::toResponse).toList(),
                "total", page.getTotalElements()
        );
    }

    private DiscountVerificationSubmissionResponse toResponse(DiscountVerificationSubmission submission) {
        DiscountVerificationSubmissionResponse response = new DiscountVerificationSubmissionResponse();
        response.setId(submission.getId());
        response.setUserId(submission.getUser() != null ? submission.getUser().getId() : null);
        response.setType(submission.getType());
        response.setStatus(submission.getStatus());
        response.setStoragePath(submission.getStoragePath());
        response.setOriginalFilename(submission.getOriginalFilename());
        response.setMimeType(submission.getMimeType());
        response.setSizeBytes(submission.getSizeBytes());
        response.setSubmittedAt(submission.getSubmittedAt());
        response.setReviewedAt(submission.getReviewedAt());
        response.setReviewerUserId(submission.getReviewerUserId());
        response.setRejectReason(submission.getRejectReason());
        response.setVersion(submission.getVersion());
        return response;
    }
}
