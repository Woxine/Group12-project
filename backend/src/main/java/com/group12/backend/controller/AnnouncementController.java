package com.group12.backend.controller;

import com.group12.backend.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/announcements")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @GetMapping("/active")
    public ResponseEntity<Object> getActive() {
        List<Map<String, Object>> list = announcementService.getActiveAnnouncements();
        return ResponseEntity.ok(Map.of("data", list));
    }
}
