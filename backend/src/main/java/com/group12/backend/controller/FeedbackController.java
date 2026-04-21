package com.group12.backend.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.group12.backend.dto.FeedbackRequest;
import com.group12.backend.dto.ProcessFeedbackRequest;
import com.group12.backend.dto.UpdateFeedbackRequest;
import com.group12.backend.exception.BusinessException;
import com.group12.backend.exception.ErrorMessages;
import com.group12.backend.security.AdminAccessGuard;
import com.group12.backend.service.FeedbackService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * 负责处理用户反馈提交与管理员反馈处理等接口请求。
 */
@RestController
@RequestMapping("/api/v1/feedbacks")
public class FeedbackController {
    
    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private AdminAccessGuard adminAccessGuard;

    // API-007: 提交用户反馈
    /**
     * 提交反馈
     * 用户提交关于行程或车辆的反馈
     */
    @PostMapping
    public ResponseEntity<Object> submit(@Valid @RequestBody FeedbackRequest request, HttpServletRequest httpRequest) {
        Long userId = extractUserId(httpRequest);
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorMessages.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("data", feedbackService.submitFeedback(userId, request)));
    }

    private Long extractUserId(HttpServletRequest request) {
        Object value = request.getAttribute("userId");
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value instanceof String str) {
            try {
                return Long.parseLong(str);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    @GetMapping
    public ResponseEntity<Object> getFeedbacks(
            @RequestParam(required = false) Boolean resolved,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            HttpServletRequest request) {
        adminAccessGuard.requireAdmin(request);
        return ResponseEntity.ok(feedbackService.getFeedbacks(resolved, priority, page, size));
    }

    // API-009: 更新反馈处理状态
    /**
     * 处理/更新反馈
     * 管理员更新反馈的处理状态
     */
    @PutMapping("/{feedbackId}")
    public ResponseEntity<Object> handle(
            @PathVariable String feedbackId,
            @Valid @RequestBody UpdateFeedbackRequest request,
            HttpServletRequest httpRequest) {
        adminAccessGuard.requireAdmin(httpRequest);
        return ResponseEntity.ok(Map.of("data", feedbackService.updateFeedback(feedbackId, request)));
    }

    /**
     * ID14 TODO: 按优先级处理反馈（低优先级直接处理，高优先级执行上报）。
     */
    @PutMapping("/{feedbackId}/process-priority")
    public ResponseEntity<Object> processByPriority(
            @PathVariable String feedbackId,
            @Valid @RequestBody ProcessFeedbackRequest request,
            HttpServletRequest httpRequest) {
        adminAccessGuard.requireAdmin(httpRequest);
        Long operatorUserId = extractUserId(httpRequest);
        return ResponseEntity.ok(Map.of("data",
                feedbackService.processFeedbackByPriority(feedbackId, request, operatorUserId)));
    }

    /**
     * ID15 TODO: 查询高优先级问题（默认管理员使用）。
     */
    @GetMapping("/high-priority")
    public ResponseEntity<Object> getHighPriorityIssues(
            @RequestParam(required = false) Boolean escalated,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            HttpServletRequest request) {
        adminAccessGuard.requireAdmin(request);
        return ResponseEntity.ok(feedbackService.getHighPriorityIssues(escalated, page, size));
    }
}
