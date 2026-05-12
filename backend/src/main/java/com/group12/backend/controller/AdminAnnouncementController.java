package com.group12.backend.controller;

import com.group12.backend.dto.AnnouncementRequest;
import com.group12.backend.security.AdminAccessGuard;
import com.group12.backend.service.AnnouncementService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/announcements")
public class AdminAnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private AdminAccessGuard adminAccessGuard;

    @GetMapping
    public ResponseEntity<Object> listAll(HttpServletRequest request) {
        adminAccessGuard.requireAdmin(request);
        List<Map<String, Object>> list = announcementService.getAllAnnouncements();
        return ResponseEntity.ok(Map.of("data", list));
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody AnnouncementRequest request,
                                         HttpServletRequest httpRequest) {
        adminAccessGuard.requireAdmin(httpRequest);
        Map<String, Object> created = announcementService.createAnnouncement(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("data", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id,
                                         @Valid @RequestBody AnnouncementRequest request,
                                         HttpServletRequest httpRequest) {
        adminAccessGuard.requireAdmin(httpRequest);
        Map<String, Object> updated = announcementService.updateAnnouncement(id, request);
        return ResponseEntity.ok(Map.of("data", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id,
                                         HttpServletRequest httpRequest) {
        adminAccessGuard.requireAdmin(httpRequest);
        announcementService.deleteAnnouncement(id);
        return ResponseEntity.ok(Map.of("message", "Announcement deleted"));
    }
}
