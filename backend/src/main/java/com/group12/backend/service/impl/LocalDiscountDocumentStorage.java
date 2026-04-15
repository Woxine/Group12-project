package com.group12.backend.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.group12.backend.config.DiscountVerificationStorageProperties;
import com.group12.backend.exception.BusinessException;
import com.group12.backend.service.DiscountDocumentStorage;

@Component
public class LocalDiscountDocumentStorage implements DiscountDocumentStorage {

    private final DiscountVerificationStorageProperties storageProperties;

    public LocalDiscountDocumentStorage(DiscountVerificationStorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    @Override
    public StoredDiscountDocument store(Long userId, String type, int version, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Uploaded file is required", HttpStatus.BAD_REQUEST);
        }
        String mimeType = file.getContentType() == null ? "" : file.getContentType().trim().toLowerCase(Locale.ROOT);
        if (!storageProperties.getAllowedMimeTypes().contains(mimeType)) {
            throw new BusinessException("Unsupported file type", HttpStatus.BAD_REQUEST);
        }
        if (file.getSize() > storageProperties.getMaxFileSizeBytes()) {
            throw new BusinessException("File exceeds max allowed size", HttpStatus.BAD_REQUEST);
        }

        String originalFileName = sanitizeFileName(file.getOriginalFilename());
        String extension = extractExtension(originalFileName);
        String uniqueName = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now())
                + "_" + UUID.randomUUID().toString().replace("-", "")
                + extension;
        String relativePath = Paths.get(String.valueOf(userId), type.toLowerCase(Locale.ROOT), "v" + version, uniqueName).toString();
        Path rootDir = Paths.get(storageProperties.getRootDir()).normalize();
        Path targetPath = rootDir.resolve(relativePath).normalize();

        if (!targetPath.startsWith(rootDir)) {
            throw new BusinessException("Invalid storage path", HttpStatus.BAD_REQUEST);
        }

        try {
            Files.createDirectories(targetPath.getParent());
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new BusinessException("Failed to store file", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new StoredDiscountDocument(
                relativePath.replace("\\", "/"),
                originalFileName,
                mimeType,
                file.getSize()
        );
    }

    private static String sanitizeFileName(String originalFileName) {
        String safe = originalFileName == null ? "document" : originalFileName;
        safe = Paths.get(safe).getFileName().toString();
        safe = safe.replace("..", "");
        if (safe.isBlank()) {
            return "document";
        }
        return safe;
    }

    private static String extractExtension(String fileName) {
        int idx = fileName.lastIndexOf('.');
        if (idx <= 0 || idx == fileName.length() - 1) {
            return "";
        }
        String ext = fileName.substring(idx).toLowerCase(Locale.ROOT);
        if (ext.length() > 10) {
            return "";
        }
        return ext;
    }
}
