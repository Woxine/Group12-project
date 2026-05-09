package com.group12.backend.sprint3state1.discount;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockMultipartFile;

import com.group12.backend.config.DiscountVerificationStorageProperties;
import com.group12.backend.exception.BusinessException;
import com.group12.backend.service.DiscountDocumentStorage.StoredDiscountDocument;
import com.group12.backend.service.impl.LocalDiscountDocumentStorage;

@DisplayName("LocalDiscountDocumentStorage")
class LocalDiscountDocumentStorageTest {

    @TempDir
    Path tempDir;

    @ParameterizedTest
    @ValueSource(strings = {"image/jpeg", "image/jpg", "image/heic", "image/heif"})
    @DisplayName("store accepts common camera image mime types")
    void store_acceptsCommonCameraImageMimeTypes(String mimeType) {
        LocalDiscountDocumentStorage storage = storageWithDefaults();
        MockMultipartFile file = new MockMultipartFile("file", "camera-photo.jpg", mimeType, "image".getBytes());

        StoredDiscountDocument stored = storage.store(1L, "STUDENT", 1, file);

        assertThat(stored.mimeType()).isEqualTo(mimeType);
        assertThat(Files.exists(tempDir.resolve(stored.storagePath()))).isTrue();
    }

    @Test
    @DisplayName("store rejects unsupported mime type")
    void store_rejectsUnsupportedMimeType() {
        LocalDiscountDocumentStorage storage = storageWithDefaults();
        MockMultipartFile file = new MockMultipartFile("file", "photo.webp", "image/webp", "image".getBytes());

        assertThatThrownBy(() -> storage.store(1L, "STUDENT", 1, file))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Unsupported file type");
    }

    @Test
    @DisplayName("store rejects files larger than configured limit")
    void store_rejectsFilesLargerThanConfiguredLimit() {
        DiscountVerificationStorageProperties properties = new DiscountVerificationStorageProperties();
        properties.setRootDir(tempDir.toString());
        properties.setMaxFileSizeBytes(3);
        LocalDiscountDocumentStorage storage = new LocalDiscountDocumentStorage(properties);
        MockMultipartFile file = new MockMultipartFile("file", "camera-photo.jpg", "image/jpeg", "image".getBytes());

        assertThatThrownBy(() -> storage.store(1L, "STUDENT", 1, file))
                .isInstanceOf(BusinessException.class)
                .hasMessage("File exceeds max allowed size");
    }

    private LocalDiscountDocumentStorage storageWithDefaults() {
        DiscountVerificationStorageProperties properties = new DiscountVerificationStorageProperties();
        properties.setRootDir(tempDir.toString());
        return new LocalDiscountDocumentStorage(properties);
    }
}
