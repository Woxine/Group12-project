package com.group12.backend.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group12.backend.entity.Feedback;
import com.group12.backend.repository.FeedbackRepository;
import com.group12.backend.security.AdminAccessGuard;
import com.group12.backend.service.FeedbackDocumentStorage;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/admin/feedbacks")
public class AdminFeedbackController {

    private final FeedbackRepository feedbackRepository;
    private final FeedbackDocumentStorage feedbackDocumentStorage;
    private final AdminAccessGuard adminAccessGuard;

    public AdminFeedbackController(
            FeedbackRepository feedbackRepository,
            FeedbackDocumentStorage feedbackDocumentStorage,
            AdminAccessGuard adminAccessGuard) {
        this.feedbackRepository = feedbackRepository;
        this.feedbackDocumentStorage = feedbackDocumentStorage;
        this.adminAccessGuard = adminAccessGuard;
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<byte[]> getFile(
            @PathVariable Long id,
            HttpServletRequest request) {
        adminAccessGuard.requireAdmin(request);

        Feedback feedback = feedbackRepository.findById(id).orElse(null);
        if (feedback == null || feedback.getImagePath() == null || feedback.getImagePath().isBlank()) {
            return ResponseEntity.notFound().build();
        }

        byte[] data = feedbackDocumentStorage.load(feedback.getImagePath());
        if (data == null) return ResponseEntity.notFound().build();

        String mimeType = feedback.getImageMimeType();
        MediaType mediaType = MediaType.parseMediaType(mimeType != null ? mimeType : "application/octet-stream");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setContentLength(data.length);
        headers.set("Content-Disposition", "attachment");

        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }
}
