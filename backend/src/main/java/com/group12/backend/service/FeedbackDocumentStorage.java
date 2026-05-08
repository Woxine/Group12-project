package com.group12.backend.service;

import org.springframework.web.multipart.MultipartFile;

public interface FeedbackDocumentStorage {
    StoredFeedbackImage store(Long userId, MultipartFile file);

    byte[] load(String storagePath);

    record StoredFeedbackImage(String storagePath, String originalFilename, String mimeType, long sizeBytes) {
    }
}
