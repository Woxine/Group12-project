package com.group12.backend.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.group12.backend.dto.ScooterResponse;
import com.group12.backend.entity.Scooter;
import com.group12.backend.repository.ScooterRepository;
import com.group12.backend.service.ScooterService;

/**
 * 实现滑板车列表查询、位置查询和车辆信息更新相关的业务逻辑。
 */
@Service
public class ScooterServiceImpl implements ScooterService {

    @Autowired
    private ScooterRepository scooterRepository;

    @Override
    /**
     * 查询滑板车列表，并按需应用状态过滤和分页截取。
     */
    public List<Object> getScooters(String status, Integer page, Integer limit) {
        List<Scooter> scooters;
        if (status != null && !status.isEmpty()) {
            scooters = scooterRepository.findByStatus(status);
        } else {
            scooters = scooterRepository.findAll();
        }

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
    /**
     * 获取指定滑板车的当前位置信息。
     */
    public Object getScooterLocation(String scooterId) {
        Long id = Long.parseLong(scooterId);
        Scooter scooter = scooterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scooter not found"));
        
        return mapToDTO(scooter);
    }

    @Override
    /**
     * 更新指定滑板车的状态、计费或经纬度信息。
     */
    public Object updateScooter(Long id, String status, BigDecimal hourRate, Double locationLat, Double locationLng) {
        Scooter scooter = scooterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scooter not found with id: " + id));

        if (status != null && !status.isEmpty()) {
            scooter.setStatus(status);
        }
        if (hourRate != null) {
            scooter.setHourRate(hourRate);
        }
        if (locationLat != null) {
            scooter.setLocationLat(locationLat);
        }
        if (locationLng != null) {
            scooter.setLocationLng(locationLng);
        }

        Scooter saved = scooterRepository.save(scooter);
        return mapToDTO(saved);
    }

    /**
     * 将滑板车实体转换为接口返回使用的 DTO。
     */
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
