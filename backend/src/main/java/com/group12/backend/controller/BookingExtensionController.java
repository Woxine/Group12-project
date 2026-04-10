package com.group12.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group12.backend.dto.ExtendBookingRequest;
import com.group12.backend.service.BookingExtensionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * TODO(ID10&11): 预订状态管理与延长控制器骨架（仅 TODO，不含业务实现）。
 */
@RestController
@RequestMapping("/api/v1/bookings")
public class BookingExtensionController {
    private final BookingExtensionService bookingExtensionService;

    public BookingExtensionController(BookingExtensionService bookingExtensionService) {
        this.bookingExtensionService = bookingExtensionService;
    }

    @PatchMapping("/{bookingId}/extend")
    public ResponseEntity<Object> extendBooking(@PathVariable String bookingId,
                                                @Valid @RequestBody ExtendBookingRequest body,
                                                HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Object result = bookingExtensionService.extendBooking(bookingId, body, userId);
        return ResponseEntity.ok(java.util.Map.of("data", result));
    }
}
