package com.group12.backend.service;

import org.springframework.web.multipart.MultipartFile;

public interface DiscountDocumentStorage {
    StoredDiscountDocument store(Long userId, String type, int version, MultipartFile file);

    /** Load file bytes by relative storagePath. Returns null if not found. */
    byte[] load(String storagePath);

    record StoredDiscountDocument(String storagePath, String originalFilename, String mimeType, long sizeBytes) {
    }
}
