package com.group12.backend.controller;

import java.util.HashMap;
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

import com.group12.backend.annotation.LogAction;
import com.group12.backend.dto.BookingResponse;
import com.group12.backend.dto.CreateBookingRequest;
import com.group12.backend.service.BookingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    /** 将 BookingResponse 转为 Map，避免 Jackson 直接序列化 DTO 报 Type definition error */
    private static Map<String, Object> toMap(BookingResponse r) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", r.getId());
        m.put("scooterId", r.getScooterId());
        m.put("userId", r.getUserId());
        m.put("status", r.getStatus());
        m.put("createdAt", r.getCreatedAt());
        return m;
    }

    // API-004
    /**
     * 创建预订
     * 提交新的用车预订请求
     */
    @LogAction(action = "CREATE_BOOKING", entityName = "Booking")
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody CreateBookingRequest request) {
        BookingResponse result = (BookingResponse) bookingService.createBooking(request);
        Map<String, Object> body = new HashMap<>();
        body.put("data", toMap(result));
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    // API-006
    /**
     * 取消预订
     * 根据预订ID取消行程
     */
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Map<String, Object>> cancel(@PathVariable String bookingId) {
        Object result = bookingService.cancelBooking(bookingId);
        Map<String, Object> body = new HashMap<>();
        body.put("data", result);
        return ResponseEntity.ok(body);
    }
}

