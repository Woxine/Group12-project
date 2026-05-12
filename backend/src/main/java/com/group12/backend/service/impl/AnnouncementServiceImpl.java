package com.group12.backend.service.impl;

import com.group12.backend.dto.AnnouncementRequest;
import com.group12.backend.entity.Announcement;
import com.group12.backend.exception.BusinessException;
import com.group12.backend.repository.AnnouncementRepository;
import com.group12.backend.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Override
    public List<Map<String, Object>> getActiveAnnouncements() {
        List<Announcement> list = announcementRepository.findActiveAnnouncements(LocalDateTime.now());
        List<Map<String, Object>> result = new ArrayList<>();
        for (Announcement a : list) {
            result.add(toResponse(a));
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getAllAnnouncements() {
        List<Announcement> list = announcementRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Announcement a : list) {
            result.add(toResponse(a));
        }
        return result;
    }

    @Override
    public Map<String, Object> createAnnouncement(AnnouncementRequest request) {
        Announcement entity = new Announcement();
        entity.setTitle(request.getTitle());
        entity.setContent(request.getContent());
        entity.setType(request.getType());
        entity.setStartTime(parseDateTime(request.getStartTime()));
        entity.setEndTime(parseDateTime(request.getEndTime()));
        entity.setEnabled(request.getEnabled() != null ? request.getEnabled() : true);
        Announcement saved = announcementRepository.save(entity);
        return toResponse(saved);
    }

    @Override
    public Map<String, Object> updateAnnouncement(Long id, AnnouncementRequest request) {
        Announcement entity = announcementRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Announcement not found", HttpStatus.NOT_FOUND));
        if (request.getTitle() != null) entity.setTitle(request.getTitle());
        if (request.getContent() != null) entity.setContent(request.getContent());
        if (request.getType() != null) entity.setType(request.getType());
        if (request.getStartTime() != null) entity.setStartTime(parseDateTime(request.getStartTime()));
        if (request.getEndTime() != null) entity.setEndTime(parseDateTime(request.getEndTime()));
        if (request.getEnabled() != null) entity.setEnabled(request.getEnabled());
        Announcement saved = announcementRepository.save(entity);
        return toResponse(saved);
    }

    @Override
    public void deleteAnnouncement(Long id) {
        if (!announcementRepository.existsById(id)) {
            throw new BusinessException("Announcement not found", HttpStatus.NOT_FOUND);
        }
        announcementRepository.deleteById(id);
    }

    private Map<String, Object> toResponse(Announcement a) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", a.getId());
        map.put("title", a.getTitle());
        map.put("content", a.getContent());
        map.put("type", a.getType());
        map.put("startTime", a.getStartTime() != null ? a.getStartTime().toString() : null);
        map.put("endTime", a.getEndTime() != null ? a.getEndTime().toString() : null);
        map.put("enabled", a.getEnabled());
        map.put("createdAt", a.getCreatedAt() != null ? a.getCreatedAt().toString() : null);
        return map;
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            return null;
        }
    }
}
