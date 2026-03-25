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

import jakarta.validation.Valid;

/**
 * 负责处理用户反馈提交与管理员反馈处理等接口请求。
 */
@RestController
@RequestMapping("/api/v1/feedbacks")
public class FeedbackController {
    
    @Autowired
    private FeedbackService feedbackService;

    // API-007: 提交用户反馈
    /**
     * 提交反馈
     * 用户提交关于行程或车辆的反馈
     */
    @PostMapping
    public ResponseEntity<Object> submit(@Valid @RequestBody FeedbackRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("data", feedbackService.submitFeedback(request)));
    }

    // API-009: 更新反馈处理状态
    /**
     * 处理/更新反馈
     * 管理员更新反馈的处理状态
     */
    @PutMapping("/{feedbackId}")
    public ResponseEntity<Object> handle(@PathVariable String feedbackId, @Valid @RequestBody UpdateFeedbackRequest request) {
        return ResponseEntity.ok(Map.of("data", feedbackService.updateFeedback(feedbackId, request)));
    }
}
