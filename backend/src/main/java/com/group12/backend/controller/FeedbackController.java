package com.group12.backend.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group12.backend.dto.FeedbackRequest;
import com.group12.backend.dto.UpdateFeedbackRequest;
import com.group12.backend.service.FeedbackService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/feedbacks")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    // API-007
    /**
     * 提交反馈
     * 用户提交关于行程或车辆的反馈（当前用户 ID 从 JWT 解析得到）
     */
    @PostMapping
    public ResponseEntity<Object> submit(@Valid @RequestBody FeedbackRequest request, HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized", "message", "User not identified"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("data", feedbackService.submitFeedback(request, userId)));
    }

    // API-009
    /**
     * 处理/更新反馈
     * 管理员更新反馈的处理状态
     */
    @PutMapping("/{feedbackId}")
    public ResponseEntity<Object> handle(@PathVariable String feedbackId, @Valid @RequestBody UpdateFeedbackRequest request) {
        return ResponseEntity.ok(Map.of("data", feedbackService.updateFeedback(feedbackId, request)));
    }
}

