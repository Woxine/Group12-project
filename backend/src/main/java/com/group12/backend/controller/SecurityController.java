package com.group12.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group12.backend.dto.SecuritySettingsRequest;
import com.group12.backend.service.SecurityService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * TODO(ID3): 安全增强控制器骨架（仅 TODO，不含业务实现）。
 */
@RestController
@RequestMapping("/api/v1/security")
public class SecurityController {
    private final SecurityService securityService;

    public SecurityController(SecurityService securityService) {
        this.securityService = securityService;
    }

    @GetMapping("/settings")
    public ResponseEntity<Object> getSettings(HttpServletRequest request) {
        // TODO: 从 request 读取 userId
        // TODO: 调用 securityService.getSecuritySettings(...)
        throw new UnsupportedOperationException("TODO: implement getSettings endpoint");
    }

    @PostMapping("/settings")
    public ResponseEntity<Object> updateSettings(@Valid @RequestBody SecuritySettingsRequest body,
                                                 HttpServletRequest request) {
        // TODO: 从 request 读取 userId
        // TODO: 调用 securityService.updateSecuritySettings(...)
        throw new UnsupportedOperationException("TODO: implement updateSettings endpoint");
    }
}
