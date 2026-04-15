package com.group12.backend.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.group12.backend.dto.RejectDiscountVerificationRequest;
import com.group12.backend.security.AdminAccessGuard;
import com.group12.backend.service.DiscountVerificationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/admin/discount-verifications")
public class AdminDiscountVerificationController {

    private final DiscountVerificationService discountVerificationService;
    private final AdminAccessGuard adminAccessGuard;

    public AdminDiscountVerificationController(
            DiscountVerificationService discountVerificationService,
            AdminAccessGuard adminAccessGuard) {
        this.discountVerificationService = discountVerificationService;
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
