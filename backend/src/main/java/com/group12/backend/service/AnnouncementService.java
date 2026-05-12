package com.group12.backend.service;

import com.group12.backend.dto.AnnouncementRequest;
import java.util.List;
import java.util.Map;

public interface AnnouncementService {
    List<Map<String, Object>> getActiveAnnouncements();
    List<Map<String, Object>> getAllAnnouncements();
    Map<String, Object> createAnnouncement(AnnouncementRequest request);
    Map<String, Object> updateAnnouncement(Long id, AnnouncementRequest request);
    void deleteAnnouncement(Long id);
}
