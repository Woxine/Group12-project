package com.group12.backend.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group12.backend.dto.CreateBookingRequest;
import com.group12.backend.service.BookingService;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {
    
    @Autowired
    private BookingService bookingService;

    // API-004
    /**
     * 创建预订
     * 提交新的用车预订请求
     */
    @PostMapping
    public ResponseEntity<Object> create(@RequestBody CreateBookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("data", bookingService.createBooking(request)));
    }

    // API-006
    /**
     * 取消预订
     * 根据预订ID取消行程
     */
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Object> cancel(@PathVariable String bookingId) {
        return ResponseEntity.ok(Map.of("data", bookingService.cancelBooking(bookingId)));
    }
}

