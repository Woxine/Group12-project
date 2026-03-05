package com.group12.backend.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group12.backend.dto.LoginRequest;
import com.group12.backend.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;

    // API-003
    /**
     * 用户登录
     * 验证用户凭据并返回登录信息
     */
    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(Map.of("data", authService.login(request)));
    }

    // API-011
    /**
     * 检查权限
     * 验证当前用户的登录状态或权限
     */
    @GetMapping("/check")
    public ResponseEntity<Object> check() {
        return ResponseEntity.ok(Map.of("data", authService.checkPermission()));
    }
}

