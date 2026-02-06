package com.group12.backend.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.group12.backend.entity.Scooter;
import com.group12.backend.repository.ScooterRepository;
import com.group12.backend.service.ScooterService;

@Service
public class ScooterServiceImpl implements ScooterService {

    @Autowired
    private ScooterRepository scooterRepository;

    @Override
    public List<Object> getScooters(String status, Integer page, Integer limit) {
        // 简化处理：如果状态存在则按状态查，否则查所有
        List<Scooter> scooters;
        if (status != null && !status.isEmpty()) {
            scooters = scooterRepository.findByStatus(status);
        } else {
            scooters = scooterRepository.findAll();
        }

        // 简单的 Java 内存分页 (Stream API)
        // 注意：生产环境应该使用 JPA Pageable 进行数据库分页
        if (page != null && limit != null && page > 0 && limit > 0) {
            int skip = (page - 1) * limit;
            return scooters.stream()
                    .skip(skip)
                    .limit(limit)
                    // 强转泛型，为了匹配 List<Object>
                    .map(s -> (Object) s) 
                    .toList();
        }

        // 强转类型
        return (List<Object>)(List<?>) scooters;
    }

    @Override
    public Object getScooterLocation(String scooterId) {
        Long id = Long.parseLong(scooterId);
        Scooter scooter = scooterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scooter not found"));
        
        // 返回包含经纬度的 Map
        return Map.of(
            "id", scooter.getId(),
            "lat", scooter.getLocationLat() != null ? scooter.getLocationLat() : 0.0,
            "lng", scooter.getLocationLng() != null ? scooter.getLocationLng() : 0.0,
            "status", scooter.getStatus()
        );
    }
}
