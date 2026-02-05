package com.group12.backend.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group12.backend.dto.RegisterRequest;
import com.group12.backend.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    
    @Autowired
    private UserService userService;

    // API-002
    /**
     * 用户注册
     * 创建新的用户账户
     */
    @PostMapping
    public ResponseEntity<Object> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("data", userService.register(request)));
    }

    // API-005
    /**
     * 获取用户历史订单
     * 查询指定用户的预订记录
     */
    @GetMapping("/{userId}/bookings")
    public ResponseEntity<Object> getHistory(@PathVariable String userId) {
        return ResponseEntity.ok(Map.of("data", userService.getUserBookings(userId)));
    }
}

