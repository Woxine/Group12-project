package com.group12.backend.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.group12.backend.dto.ScooterResponse;
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
                    .map(this::mapToDTO)
                    .map(dto -> (Object) dto)
                    .toList();
        }

        return scooters.stream()
                .map(this::mapToDTO)
                .map(dto -> (Object) dto)
                .toList();
    }

    @Override
    public Object getScooterLocation(String scooterId) {
        Long id = Long.parseLong(scooterId);
        Scooter scooter = scooterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scooter not found"));
        
        // Return DTO instead of Map or Entity
        return mapToDTO(scooter);
    }

    private ScooterResponse mapToDTO(Scooter scooter) {
        String locName = (scooter.getLocationPoint() != null) ? scooter.getLocationPoint().getName() : "Unknown";
        return new ScooterResponse(
            scooter.getId(),
            scooter.getStatus(),
            scooter.getLocationLat(),
            scooter.getLocationLng(),
            scooter.getHourRate(),
            locName
        );
    }
}
