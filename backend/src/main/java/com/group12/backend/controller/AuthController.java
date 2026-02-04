package com.group12.backend.controller;

import com.group12.backend.dto.*;
import com.group12.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;

    // API-003
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(Map.of("data", authService.login(request)));
    }

    // API-011
    @GetMapping("/check")
    public ResponseEntity<Object> check() {
        return ResponseEntity.ok(Map.of("data", authService.checkPermission()));
    }
}

