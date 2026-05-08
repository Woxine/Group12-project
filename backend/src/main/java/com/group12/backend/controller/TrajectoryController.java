package com.group12.backend.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group12.backend.dto.UploadTrajectoryRequest;
import com.group12.backend.exception.BusinessException;
import com.group12.backend.exception.ErrorMessages;
import com.group12.backend.service.TrajectoryService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/bookings")
public class TrajectoryController {
    private final TrajectoryService trajectoryService;

    public TrajectoryController(TrajectoryService trajectoryService) {
        this.trajectoryService = trajectoryService;
    }

    @PostMapping("/{bookingId}/trajectory")
    public ResponseEntity<Object> upload(
            @PathVariable String bookingId,
            @Valid @RequestBody UploadTrajectoryRequest request,
            HttpServletRequest httpRequest) {
        Long authUserId = extractAuthUserId(httpRequest);
        return ResponseEntity.ok(Map.of("data", trajectoryService.uploadTrajectory(bookingId, authUserId, request)));
    }

    @GetMapping("/{bookingId}/trajectory")
    public ResponseEntity<Object> getTrajectory(
            @PathVariable String bookingId,
            HttpServletRequest httpRequest) {
        Long authUserId = extractAuthUserId(httpRequest);
        return ResponseEntity.ok(Map.of("data", trajectoryService.getTrajectory(bookingId, authUserId)));
    }

    private Long extractAuthUserId(HttpServletRequest httpRequest) {
        Object authUserId = httpRequest.getAttribute("userId");
        if (authUserId == null) {
            throw new BusinessException(ErrorMessages.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }
        return Long.parseLong(String.valueOf(authUserId));
    }
}
