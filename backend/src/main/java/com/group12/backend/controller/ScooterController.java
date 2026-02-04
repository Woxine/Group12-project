package com.group12.backend.controller;

import com.group12.backend.service.ScooterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/scooters")
public class ScooterController {
    
    @Autowired
    private ScooterService scooterService;

    // API-001
    @GetMapping
    public ResponseEntity<Object> getScooters(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit) {
        return ResponseEntity.ok(Map.of("data", scooterService.getScooters(status, page, limit)));
    }

    // API-010
    @GetMapping("/{scooterId}/location")
    public ResponseEntity<Object> getLocation(@PathVariable String scooterId) {
        return ResponseEntity.ok(Map.of("data", scooterService.getScooterLocation(scooterId)));
    }
}

