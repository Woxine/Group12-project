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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.group12.backend.dto.RegisterRequest;
import com.group12.backend.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Object> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("data", userService.register(request)));
    }

    @GetMapping("/{userId}/bookings")
    public ResponseEntity<Object> getHistory(
            @PathVariable String userId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return ResponseEntity.ok(userService.getUserBookings(userId, page, size));
    }

    @GetMapping("/{userId}/bookings/{bookingId}")
    public ResponseEntity<Object> getBookingById(
            @PathVariable String userId,
            @PathVariable String bookingId) {
        return ResponseEntity.ok(userService.getBookingById(userId, bookingId));
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<Object> getProfile(@PathVariable String userId) {
        return ResponseEntity.ok(Map.of("data", userService.getUserProfile(userId)));
    }
}
