package com.group12.backend.controller;

import com.group12.backend.dto.*;
import com.group12.backend.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {
    
    @Autowired
    private BookingService bookingService;

    // API-004
    @PostMapping
    public ResponseEntity<Object> create(@RequestBody CreateBookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("data", bookingService.createBooking(request)));
    }

    // API-006
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Object> cancel(@PathVariable String bookingId) {
        return ResponseEntity.ok(Map.of("data", bookingService.cancelBooking(bookingId)));
    }
}

