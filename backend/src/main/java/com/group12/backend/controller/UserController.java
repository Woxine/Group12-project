package com.group12.backend.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

import com.group12.backend.dto.BookingResponse;
import com.group12.backend.dto.RegisterRequest;
import com.group12.backend.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    private static Map<String, Object> bookingToMap(BookingResponse r) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", r.getId());
        m.put("scooterId", r.getScooterId());
        m.put("userId", r.getUserId());
        m.put("status", r.getStatus());
        m.put("startTime", r.getStartTime());
        m.put("endTime", r.getEndTime());
        m.put("duration", r.getDuration());
        m.put("total_price", r.getTotalPrice());
        m.put("price", r.getTotalPrice());
        m.put("createdAt", r.getCreatedAt());
        return m;
    }

    // API-002
    /**
     * 用户注册
     * 创建新的用户账户
     */
    @PostMapping
    public ResponseEntity<Object> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("data", userService.register(request)));
    }

    // API-005
    /**
     * 获取用户历史订单
     * 查询指定用户的预订记录（转为 Map 列表避免 Jackson 序列化 BookingResponse 报错）
     */
    @GetMapping("/{userId}/bookings")
    public ResponseEntity<Object> getHistory(@PathVariable String userId) {
        List<Object> raw = userService.getUserBookings(userId);
        List<Map<String, Object>> data = new ArrayList<>();
        for (Object o : raw) {
            if (o instanceof BookingResponse) {
                data.add(bookingToMap((BookingResponse) o));
            }
        }
        return ResponseEntity.ok(Map.of("data", data));
    }

    // API-012
    /**
     * 获取用户个人资料
     * 用于前端展示用户详情（头像、角色等）
     */
    @GetMapping("/{userId}/profile")
    public ResponseEntity<Object> getProfile(@PathVariable String userId) {
        return ResponseEntity.ok(Map.of("data", userService.getUserProfile(userId)));
    }
}

