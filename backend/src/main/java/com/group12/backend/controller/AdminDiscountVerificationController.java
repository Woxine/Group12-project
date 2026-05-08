package com.group12.backend.controller;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.group12.backend.dto.DiscountVerificationSubmissionResponse;
import com.group12.backend.dto.RejectDiscountVerificationRequest;
import com.group12.backend.security.AdminAccessGuard;
import com.group12.backend.service.DiscountDocumentStorage;
import com.group12.backend.service.DiscountVerificationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/admin/discount-verifications")
public class AdminDiscountVerificationController {

    private final DiscountVerificationService discountVerificationService;
    private final DiscountDocumentStorage discountDocumentStorage;
    private final AdminAccessGuard adminAccessGuard;

    public AdminDiscountVerificationController(
            DiscountVerificationService discountVerificationService,
            DiscountDocumentStorage discountDocumentStorage,
            AdminAccessGuard adminAccessGuard) {
        this.discountVerificationService = discountVerificationService;
        this.discountDocumentStorage = discountDocumentStorage;
        this.adminAccessGuard = adminAccessGuard;
    }

    @GetMapping
    public ResponseEntity<Object> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            HttpServletRequest request) {
        adminAccessGuard.requireAdmin(request);
        return ResponseEntity.ok(discountVerificationService.getAdminSubmissions(status, type, page, size));
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<byte[]> getFile(
            @PathVariable Long id,
            HttpServletRequest request) {
        adminAccessGuard.requireAdmin(request);

        Object obj = discountVerificationService.getSubmissionById(id);
        if (obj == null) return ResponseEntity.notFound().build();

        DiscountVerificationSubmissionResponse submission = (DiscountVerificationSubmissionResponse) obj;
        byte[] data = discountDocumentStorage.load(submission.getStoragePath());
        if (data == null) return ResponseEntity.notFound().build();

        String mimeType = submission.getMimeType();
        MediaType mediaType = MediaType.parseMediaType(mimeType != null ? mimeType : "application/octet-stream");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setContentLength(data.length);
        headers.set("Content-Disposition", "inline; filename=\"" + submission.getOriginalFilename() + "\"");

        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Object> approve(
            @PathVariable Long id,
            HttpServletRequest request) {
        adminAccessGuard.requireAdmin(request);
        Long reviewerId = Long.parseLong(String.valueOf(request.getAttribute("userId")));
        return ResponseEntity.ok(Map.of("data", discountVerificationService.approve(id, reviewerId)));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Object> reject(
            @PathVariable Long id,
            @Valid @RequestBody RejectDiscountVerificationRequest requestBody,
            HttpServletRequest request) {
        adminAccessGuard.requireAdmin(request);
        Long reviewerId = Long.parseLong(String.valueOf(request.getAttribute("userId")));
        return ResponseEntity.ok(Map.of("data", discountVerificationService.reject(id, reviewerId, requestBody.getReason())));
    }
}
