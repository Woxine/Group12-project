package com.group12.backend.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.group12.backend.exception.BusinessException;
import com.group12.backend.exception.ErrorMessages;
import com.group12.backend.service.DiscountVerificationService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/users/{userId}/discount-verifications")
public class DiscountVerificationController {

    private final DiscountVerificationService discountVerificationService;

    public DiscountVerificationController(DiscountVerificationService discountVerificationService) {
        this.discountVerificationService = discountVerificationService;
    }

    @PostMapping
    public ResponseEntity<Object> submit(
            @PathVariable String userId,
            @RequestParam String type,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        Long authUserId = extractAndValidateSelf(request, userId);
        return ResponseEntity.ok(Map.of("data", discountVerificationService.submit(authUserId, type, file)));
    }

    @GetMapping
    public ResponseEntity<Object> listMine(
            @PathVariable String userId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            HttpServletRequest request) {
        Long authUserId = extractAndValidateSelf(request, userId);
        return ResponseEntity.ok(discountVerificationService.getUserSubmissions(authUserId, page, size));
    }

    private static Long extractAndValidateSelf(HttpServletRequest request, String pathUserId) {
        Object authUserId = request.getAttribute("userId");
        if (authUserId == null || !pathUserId.equals(String.valueOf(authUserId))) {
            throw new BusinessException(ErrorMessages.FORBIDDEN, HttpStatus.FORBIDDEN);
        }
        return Long.parseLong(pathUserId);
    }
}
