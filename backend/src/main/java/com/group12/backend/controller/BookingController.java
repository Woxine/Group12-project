package com.group12.backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group12.backend.annotation.LogAction;
import com.group12.backend.dto.CreateBookingRequest;
import com.group12.backend.dto.CreateGuestBookingRequest;
import com.group12.backend.service.BookingService;

import jakarta.validation.Valid;

/**
 * 负责处理预约订单的创建、取消和完成等接口请求。
 */
@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    /**
     * 创建新的滑板车预约订单，并记录起点位置等基础信息。
     */
    @LogAction(action = "CREATE_BOOKING", entityName = "Booking")
    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody CreateBookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("data", bookingService.createBooking(request)));
    }

    /**
     * 店员为未注册用户创建预约订单。
     */
    @LogAction(action = "CREATE_GUEST_BOOKING", entityName = "Booking")
    @PostMapping("/guest")
    public ResponseEntity<Object> createGuestBooking(@Valid @RequestBody CreateGuestBookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("data", bookingService.createGuestBooking(request)));
    }

    /**
     * 取消指定预约订单，并可同时记录车辆结束位置。
     */
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Map<String, Object>> cancel(
            @PathVariable String bookingId,
            @RequestBody(required = false) Map<String, Object> body) {
        Double endLat = body != null && body.get("endLat") != null ? ((Number) body.get("endLat")).doubleValue() : null;
        Double endLng = body != null && body.get("endLng") != null ? ((Number) body.get("endLng")).doubleValue() : null;
        Object result = bookingService.cancelBooking(bookingId, endLat, endLng);
        Map<String, Object> resp = new HashMap<>();
        resp.put("data", result);
        return ResponseEntity.ok(resp);
    }

    /**
     * 完成指定预约订单，并结算费用及记录还车位置。
     */
    @PatchMapping("/{bookingId}/complete")
    public ResponseEntity<Map<String, Object>> complete(
            @PathVariable String bookingId,
            @RequestBody(required = false) Map<String, Object> body) {
        Double endLat = body != null && body.get("endLat") != null ? ((Number) body.get("endLat")).doubleValue() : null;
        Double endLng = body != null && body.get("endLng") != null ? ((Number) body.get("endLng")).doubleValue() : null;
        Object result = bookingService.completeBooking(bookingId, endLat, endLng);
        Map<String, Object> res = new HashMap<>();
        res.put("data", result);
        return ResponseEntity.ok(res);
    }
}
