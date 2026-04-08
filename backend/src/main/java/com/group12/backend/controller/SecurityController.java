package com.group12.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

import com.group12.backend.dto.SecuritySettingsRequest;
import com.group12.backend.service.SecurityService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * ID3: 账户安全设置控制器。
 */
@RestController
@RequestMapping("/api/v1/security")
public class SecurityController {
    private final SecurityService securityService;

    public SecurityController(SecurityService securityService) {
        this.securityService = securityService;
    }

    @GetMapping("/settings")
    public ResponseEntity<Object> getSecuritySettings(HttpServletRequest request) {
        Long userId = extractUserId(request);
        return ResponseEntity.ok(Map.of("data", securityService.getSecuritySettings(userId)));
    }

    @PostMapping("/settings")
    public ResponseEntity<Object> updateSecuritySettings(@Valid @RequestBody SecuritySettingsRequest body,
                                                         HttpServletRequest request) {
        Long userId = extractUserId(request);
        return ResponseEntity.ok(Map.of("data", securityService.updateSecuritySettings(userId, body)));
    }

    private Long extractUserId(HttpServletRequest request) {
        Object value = request.getAttribute("userId");
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value instanceof String str) {
            try {
                return Long.parseLong(str);
            } catch (NumberFormatException ignored) {
                // Fallback to anonymous key for tests or absent interceptor context.
            }
        }
        return 0L;
    }
}
