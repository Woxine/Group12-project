package com.group12.backend.controller;

import com.group12.backend.dto.*;
import com.group12.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    
    @Autowired
    private UserService userService;

    // API-002
    @PostMapping
    public ResponseEntity<Object> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("data", userService.register(request)));
    }

    // API-005
    @GetMapping("/{userId}/bookings")
    public ResponseEntity<Object> getHistory(@PathVariable String userId) {
        return ResponseEntity.ok(Map.of("data", userService.getUserBookings(userId)));
    }
}

