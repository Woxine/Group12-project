package com.group12.backend.controller;

import com.group12.backend.dto.*;
import com.group12.backend.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/feedbacks")
public class FeedbackController {
    
    @Autowired
    private FeedbackService feedbackService;

    // API-007
    @PostMapping
    public ResponseEntity<Object> submit(@RequestBody FeedbackRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("data", feedbackService.submitFeedback(request)));
    }

    // API-009
    @PutMapping("/{feedbackId}")
    public ResponseEntity<Object> handle(@PathVariable String feedbackId, @RequestBody UpdateFeedbackRequest request) {
        return ResponseEntity.ok(Map.of("data", feedbackService.updateFeedback(feedbackId, request)));
    }
}

