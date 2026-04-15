package com.group12.backend.service;

import org.springframework.web.multipart.MultipartFile;

public interface DiscountVerificationService {
    Object submit(Long userId, String type, MultipartFile file);

    Object getUserSubmissions(Long userId, Integer page, Integer size);

    Object getAdminSubmissions(String status, String type, Integer page, Integer size);

    Object approve(Long submissionId, Long reviewerUserId);

    Object reject(Long submissionId, Long reviewerUserId, String reason);
}
