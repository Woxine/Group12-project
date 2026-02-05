package com.group12.backend.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.group12.backend.service.ScooterService;

@RestController
@RequestMapping("/api/v1/scooters")
public class ScooterController {
    
    @Autowired
    private ScooterService scooterService;

    // API-001
    /**
     * 获取滑板车列表
     * 支持按状态筛选和分页查询
     */
    @GetMapping
    public ResponseEntity<Object> getScooters(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit) {
        return ResponseEntity.ok(Map.of("data", scooterService.getScooters(status, page, limit)));
    }

    // API-010
    /**
     * 获取滑板车实时位置
     * 根据ID查询特定车辆的地理位置
     */
    @GetMapping("/{scooterId}/location")
    public ResponseEntity<Object> getLocation(@PathVariable String scooterId) {
        return ResponseEntity.ok(Map.of("data", scooterService.getScooterLocation(scooterId)));
    }
}

