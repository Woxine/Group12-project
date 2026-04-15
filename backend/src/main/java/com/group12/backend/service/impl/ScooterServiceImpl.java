package com.group12.backend.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.group12.backend.dto.CreateScooterRequest;
import com.group12.backend.dto.ScooterResponse;
import com.group12.backend.entity.LocationPoint;
import com.group12.backend.entity.Scooter;
import com.group12.backend.exception.BusinessException;
import com.group12.backend.exception.ErrorMessages;
import com.group12.backend.repository.BookingRepository;
import com.group12.backend.repository.LocationPointRepository;
import com.group12.backend.repository.ScooterRepository;
import com.group12.backend.service.BillingRule;
import com.group12.backend.service.BillingService;
import com.group12.backend.service.ScooterService;

/**
 * 实现滑板车列表查询、位置查询和车辆信息更新相关的业务逻辑。
 */
@Service
public class ScooterServiceImpl implements ScooterService {

    @Autowired
    private ScooterRepository scooterRepository;

    @Autowired
    private BillingService billingService;

    @Autowired
    private LocationPointRepository locationPointRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Override
    /**
     * 查询滑板车列表，并按需应用状态过滤和分页截取。
     */
    public Map<String, Object> getScooters(String status, Integer page, Integer size) {
        List<Scooter> scooters = listScootersByStatus(status).stream()
                .filter(ScooterServiceImpl::isPubliclyListed)
                .toList();

        long total = scooters.size();
        if (page != null && size != null && page > 0 && size > 0) {
            int skip = (page - 1) * size;
            List<Object> data = scooters.stream()
                    .skip(skip)
                    .limit(size)
                    .map(this::mapToDTO)
                    .map(dto -> (Object) dto)
                    .toList();
            return Map.of("data", data, "total", total);
        }

        List<Object> data = scooters.stream()
                .map(this::mapToDTO)
                .map(dto -> (Object) dto)
                .toList();
        return Map.of("data", data, "total", total);
    }

    @Override
    public Map<String, Object> getScootersForAdmin(String status, Integer page, Integer size) {
        List<Scooter> scooters = listScootersByStatus(status);

        long total = scooters.size();
        if (page != null && size != null && page > 0 && size > 0) {
            int skip = (page - 1) * size;
            List<Object> data = scooters.stream()
                    .skip(skip)
                    .limit(size)
                    .map(this::mapToDTO)
                    .map(dto -> (Object) dto)
                    .toList();
            return Map.of("data", data, "total", total);
        }

        List<Object> data = scooters.stream()
                .map(this::mapToDTO)
                .map(dto -> (Object) dto)
                .toList();
        return Map.of("data", data, "total", total);
    }

    private List<Scooter> listScootersByStatus(String status) {
        if (status != null && !status.isEmpty()) {
            return scooterRepository.findByStatus(status);
        }
        return scooterRepository.findAll();
    }

    private static boolean isPubliclyListed(Scooter s) {
        return s.getVisible() == null || Boolean.TRUE.equals(s.getVisible());
    }

    @Override
    /**
     * 获取指定滑板车的当前位置信息。
     */
    public Object getScooterLocation(String scooterId) {
        Long id = Long.parseLong(scooterId);
        Scooter scooter = scooterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scooter not found"));
        if (!isPubliclyListed(scooter)) {
            throw new RuntimeException("Scooter not found");
        }

        return mapToDTO(scooter);
    }

    @Override
    /**
     * 更新指定滑板车的状态、计费或经纬度信息。
     */
    public Object updateScooter(Long id, String status, BigDecimal hourRate, Double locationLat, Double locationLng, Boolean visible) {
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
        if (visible != null) {
            scooter.setVisible(visible);
        }

        Scooter saved = scooterRepository.save(scooter);
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public Object createScooter(CreateScooterRequest request) {
        Scooter scooter = new Scooter();
        String st = request.getStatus();
        scooter.setStatus(st != null && !st.isEmpty() ? st : "AVAILABLE");
        scooter.setHourRate(request.getHour_rate());
        scooter.setLocationLat(request.getLocation_lat());
        scooter.setLocationLng(request.getLocation_lng());
        scooter.setVisible(true);

        if (request.getLocation_point_id() != null) {
            locationPointRepository.findById(request.getLocation_point_id())
                    .ifPresent(scooter::setLocationPoint);
        } else if (request.getLocation_name() != null && !request.getLocation_name().isBlank()) {
            LocationPoint lp = new LocationPoint();
            lp.setName(request.getLocation_name().trim());
            lp.setLat(request.getLocation_lat());
            lp.setLng(request.getLocation_lng());
            lp = locationPointRepository.save(lp);
            scooter.setLocationPoint(lp);
        }

        Scooter saved = scooterRepository.save(scooter);
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public void deleteScooter(Long id) {
        if (!scooterRepository.existsById(id)) {
            throw new BusinessException(ErrorMessages.SCOOTER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        if (!bookingRepository.findByScooterId(id).isEmpty()) {
            throw new BusinessException("Cannot delete scooter that has booking records", HttpStatus.BAD_REQUEST);
        }
        scooterRepository.deleteById(id);
    }

    /**
     * 将滑板车实体转换为接口返回使用的 DTO。
     */
    private ScooterResponse mapToDTO(Scooter scooter) {
        BillingRule billingRule = billingService.getCurrentRule();
        String locName = (scooter.getLocationPoint() != null) ? scooter.getLocationPoint().getName() : "Unknown";
        return new ScooterResponse(
            scooter.getId(),
            scooter.getStatus(),
            scooter.getLocationLat(),
            scooter.getLocationLng(),
            scooter.getHourRate(),
            locName,
            billingRule.longRentThresholdHours(),
            billingRule.extraLongRentThresholdHours(),
            billingRule.longRentHourRateMultiplier(),
            billingRule.extraLongRentHourRateMultiplier(),
            scooter.getVisible()
        );
    }
}
