package com.group12.backend.controller;

import com.group12.backend.dto.VehicleDescriptionRequest;
import com.group12.backend.entity.VehicleDescription;
import com.group12.backend.security.AdminAccessGuard;
import com.group12.backend.service.VehicleDescriptionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/vehicles")
public class VehicleDescriptionController {

    @Autowired
    private VehicleDescriptionService vehicleDescriptionService;

    @Autowired
    private AdminAccessGuard adminAccessGuard;

    @GetMapping
    public ResponseEntity<Object> getAllDescriptions() {
        List<VehicleDescription> list = vehicleDescriptionService.getAllDescriptions();
        return ResponseEntity.ok(Map.of("data", list));
    }

    @GetMapping("/{type}")
    public ResponseEntity<Object> getDescriptionByType(@PathVariable String type) {
        VehicleDescription desc = vehicleDescriptionService.getDescriptionByType(type.toUpperCase());
        return ResponseEntity.ok(Map.of("data", desc));
    }

    @PutMapping("/{type}")
    public ResponseEntity<Object> updateDescription(@PathVariable String type,
                                                     @Valid @RequestBody VehicleDescriptionRequest request,
                                                     HttpServletRequest httpRequest) {
        adminAccessGuard.requireAdmin(httpRequest);
        VehicleDescription updated = vehicleDescriptionService.updateDescription(type.toUpperCase(), request);
        return ResponseEntity.ok(Map.of("data", updated));
    }
}
