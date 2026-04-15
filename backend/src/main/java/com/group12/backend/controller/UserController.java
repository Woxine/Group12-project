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

import com.group12.backend.dto.ChangePasswordRequest;
import com.group12.backend.dto.ChangeEmailRequest;
import com.group12.backend.dto.ChangeNameRequest;
import com.group12.backend.dto.RegisterRequest;
import com.group12.backend.dto.UpdateProfileRequest;
import com.group12.backend.exception.BusinessException;
import com.group12.backend.exception.ErrorMessages;
import com.group12.backend.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * 负责处理用户注册、个人资料查询和预约历史查询等接口请求。
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 接收新用户注册信息并创建账户。
     */
    @PostMapping
    public ResponseEntity<Object> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("data", userService.register(request)));
    }

    /**
     * 查询指定用户的预约历史记录，支持分页返回。
     */
    @GetMapping("/{userId}/bookings")
    public ResponseEntity<Object> getHistory(
            @PathVariable String userId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return ResponseEntity.ok(userService.getUserBookings(userId, page, size));
    }

    /**
     * 查询指定用户的单条预约详情。
     */
    @GetMapping("/{userId}/bookings/{bookingId}")
    public ResponseEntity<Object> getBookingById(
            @PathVariable String userId,
            @PathVariable String bookingId) {
        return ResponseEntity.ok(userService.getBookingById(userId, bookingId));
    }

    /**
     * 获取指定用户的个人资料信息。
     */
    @GetMapping("/{userId}/profile")
    public ResponseEntity<Object> getProfile(@PathVariable String userId) {
        return ResponseEntity.ok(Map.of("data", userService.getUserProfile(userId)));
    }

    @PostMapping("/{userId}/profile")
    public ResponseEntity<Object> updateProfile(
            @PathVariable String userId,
            @Valid @RequestBody UpdateProfileRequest request,
            HttpServletRequest httpRequest) {
        Object authUserId = httpRequest.getAttribute("userId");
        if (authUserId == null || !userId.equals(String.valueOf(authUserId))) {
            throw new BusinessException(ErrorMessages.FORBIDDEN, HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(Map.of("data", userService.updateUserProfile(userId, request)));
    }

    /**
     * 修改指定用户密码，需先校验原密码。
     */
    @PostMapping("/{userId}/password")
    public ResponseEntity<Object> changePassword(
            @PathVariable String userId,
            @Valid @RequestBody ChangePasswordRequest request,
            HttpServletRequest httpRequest) {
        Object authUserId = httpRequest.getAttribute("userId");
        if (authUserId == null || !userId.equals(String.valueOf(authUserId))) {
            throw new BusinessException(ErrorMessages.FORBIDDEN, HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(Map.of("data", userService.changePassword(userId, request)));
    }

    /**
     * 修改指定用户邮箱，需验证登录用户与目标用户一致。
     */
    @PostMapping("/{userId}/email")
    public ResponseEntity<Object> changeEmail(
            @PathVariable String userId,
            @Valid @RequestBody ChangeEmailRequest request,
            HttpServletRequest httpRequest) {
        Object authUserId = httpRequest.getAttribute("userId");
        if (authUserId == null || !userId.equals(String.valueOf(authUserId))) {
            throw new BusinessException(ErrorMessages.FORBIDDEN, HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(Map.of("data", userService.changeEmail(userId, request)));
    }

    /**
     * 修改指定用户名，需验证登录用户与目标用户一致。
     */
    @PostMapping("/{userId}/name")
    public ResponseEntity<Object> changeName(
            @PathVariable String userId,
            @Valid @RequestBody ChangeNameRequest request,
            HttpServletRequest httpRequest) {
        Object authUserId = httpRequest.getAttribute("userId");
        if (authUserId == null || !userId.equals(String.valueOf(authUserId))) {
            throw new BusinessException(ErrorMessages.FORBIDDEN, HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(Map.of("data", userService.changeName(userId, request)));
    }
}
