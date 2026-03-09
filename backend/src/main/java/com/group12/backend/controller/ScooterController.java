package com.group12.backend.controller;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PutMapping("/{scooterId}")
    public ResponseEntity<Object> updateScooter(
            @PathVariable Long scooterId,
            @RequestBody Map<String, Object> body) {
        String status = body.containsKey("status") ? (String) body.get("status") : null;
        BigDecimal hourRate = body.containsKey("hour_rate") ? new BigDecimal(body.get("hour_rate").toString()) : null;
        Double locationLat = body.containsKey("location_lat") ? Double.valueOf(body.get("location_lat").toString()) : null;
        Double locationLng = body.containsKey("location_lng") ? Double.valueOf(body.get("location_lng").toString()) : null;

        Object updated = scooterService.updateScooter(scooterId, status, hourRate, locationLat, locationLng);
        return ResponseEntity.ok(Map.of("data", updated));
    }
}
