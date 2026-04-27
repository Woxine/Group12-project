package com.group12.backend.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.group12.backend.dto.BulkScooterUpdateRequest;
import com.group12.backend.dto.CreateScooterRequest;
import com.group12.backend.dto.ScooterBulkApplyResponse;
import com.group12.backend.dto.ScooterBulkPreviewResponse;
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
    private static final Set<String> SUPPORTED_SCOOTER_TYPES = Set.of("GEN1", "GEN2", "GEN3", "GEN3PRO");
    private static final Set<String> SUPPORTED_SCOOTER_STATUSES = Set.of("AVAILABLE", "RESERVED", "RENTED", "MAINTENANCE");

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
    public Object updateScooter(Long id, String type, String status, BigDecimal hourRate, Double locationLat, Double locationLng, Boolean visible) {
        Scooter scooter = scooterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scooter not found with id: " + id));

        if (type != null && !type.isEmpty()) {
            scooter.setType(type);
        }
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
        String ty = request.getType();
        scooter.setType(ty != null && !ty.isEmpty() ? ty : "GEN1");
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
    public ScooterBulkPreviewResponse previewBulkUpdateByType(BulkScooterUpdateRequest request) {
        String normalizedType = normalizeAndValidateType(request.getType());
        String normalizedStatus = normalizeAndValidateStatus(request.getStatus());
        validateBulkPatchRequest(request.getHour_rate(), normalizedStatus, request.getVisible());

        List<Scooter> scooters = scooterRepository.findByType(normalizedType).stream()
                .filter(scooter -> isTypeMatch(normalizedType, scooter))
                .toList();
        boolean risky = isRiskyBulkPatch(normalizedStatus, request.getVisible());
        List<String> warnings = buildRiskWarnings(scooters, normalizedStatus, request.getVisible());

        ScooterBulkPreviewResponse response = new ScooterBulkPreviewResponse();
        response.setType(normalizedType);
        response.setMatchedCount(scooters.size());
        response.setHiddenCount(countHidden(scooters));
        response.setStatusBreakdown(buildStatusBreakdown(scooters));
        response.setRisky(risky);
        response.setRiskWarnings(warnings);
        return response;
    }

    @Override
    @Transactional
    public ScooterBulkApplyResponse applyBulkUpdateByType(BulkScooterUpdateRequest request) {
        String normalizedType = normalizeAndValidateType(request.getType());
        String normalizedStatus = normalizeAndValidateStatus(request.getStatus());
        validateBulkPatchRequest(request.getHour_rate(), normalizedStatus, request.getVisible());

        List<Scooter> scooters = scooterRepository.findByType(normalizedType).stream()
                .filter(scooter -> isTypeMatch(normalizedType, scooter))
                .toList();
        boolean risky = isRiskyBulkPatch(normalizedStatus, request.getVisible());
        List<String> warnings = buildRiskWarnings(scooters, normalizedStatus, request.getVisible());
        if (risky && !Boolean.TRUE.equals(request.getConfirm_risky())) {
            throw new BusinessException(
                    "Bulk update touches risky fields. Please preview and confirm with confirm_risky=true.",
                    HttpStatus.BAD_REQUEST);
        }

        int updatedCount = 0;
        for (Scooter scooter : scooters) {
            boolean changed = false;
            if (request.getHour_rate() != null && request.getHour_rate().compareTo(scooter.getHourRate()) != 0) {
                scooter.setHourRate(request.getHour_rate());
                changed = true;
            }
            if (normalizedStatus != null && !normalizedStatus.equals(scooter.getStatus())) {
                scooter.setStatus(normalizedStatus);
                changed = true;
            }
            if (request.getVisible() != null && !request.getVisible().equals(scooter.getVisible())) {
                scooter.setVisible(request.getVisible());
                changed = true;
            }
            if (changed) {
                updatedCount++;
            }
        }

        if (updatedCount > 0) {
            scooterRepository.saveAll(scooters);
        }

        ScooterBulkApplyResponse response = new ScooterBulkApplyResponse();
        response.setType(normalizedType);
        response.setMatchedCount(scooters.size());
        response.setUpdatedCount(updatedCount);
        response.setHiddenCount(countHidden(scooters));
        response.setStatusBreakdown(buildStatusBreakdown(scooters));
        response.setRisky(risky);
        response.setRiskWarnings(warnings);
        return response;
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

    private static String normalizeAndValidateType(String rawType) {
        if (rawType == null || rawType.isBlank()) {
            throw new BusinessException("type is required", HttpStatus.BAD_REQUEST);
        }
        String normalized = rawType.trim().toUpperCase(Locale.ROOT);
        if ("GEN3 PRO".equals(normalized) || "GEN3_PRO".equals(normalized) || "GEN-3-PRO".equals(normalized)) {
            normalized = "GEN3PRO";
        }
        if (!SUPPORTED_SCOOTER_TYPES.contains(normalized)) {
            throw new BusinessException("Unsupported scooter type: " + rawType, HttpStatus.BAD_REQUEST);
        }
        return normalized;
    }

    private static String normalizeAndValidateStatus(String rawStatus) {
        if (rawStatus == null || rawStatus.isBlank()) {
            return null;
        }
        String normalized = rawStatus.trim().toUpperCase(Locale.ROOT);
        if (!SUPPORTED_SCOOTER_STATUSES.contains(normalized)) {
            throw new BusinessException("Unsupported scooter status: " + rawStatus, HttpStatus.BAD_REQUEST);
        }
        return normalized;
    }

    private static void validateBulkPatchRequest(BigDecimal hourRate, String status, Boolean visible) {
        if (hourRate == null && status == null && visible == null) {
            throw new BusinessException("At least one patch field is required", HttpStatus.BAD_REQUEST);
        }
        if (hourRate != null && hourRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("hour_rate must be greater than 0", HttpStatus.BAD_REQUEST);
        }
    }

    private static boolean isRiskyBulkPatch(String status, Boolean visible) {
        return status != null || visible != null;
    }

    private static int countHidden(List<Scooter> scooters) {
        int hidden = 0;
        for (Scooter scooter : scooters) {
            if (Boolean.FALSE.equals(scooter.getVisible())) {
                hidden++;
            }
        }
        return hidden;
    }

    private static Map<String, Long> buildStatusBreakdown(List<Scooter> scooters) {
        Map<String, Long> result = new LinkedHashMap<>();
        for (String status : SUPPORTED_SCOOTER_STATUSES) {
            result.put(status, 0L);
        }
        Map<String, Long> computed = scooters.stream()
                .collect(Collectors.groupingBy(
                        scooter -> scooter.getStatus() == null ? "UNKNOWN" : scooter.getStatus().toUpperCase(Locale.ROOT),
                        Collectors.counting()));
        for (Map.Entry<String, Long> entry : computed.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    private static List<String> buildRiskWarnings(List<Scooter> scooters, String targetStatus, Boolean targetVisible) {
        List<String> warnings = new ArrayList<>();
        if (targetStatus != null) {
            warnings.add("Status updates can affect booking and dispatch operations.");
        }
        if (targetVisible != null) {
            warnings.add("Visibility updates can instantly change client map availability.");
        }
        long rentedCount = scooters.stream().filter(s -> "RENTED".equalsIgnoreCase(s.getStatus())).count();
        if (rentedCount > 0 && (targetStatus != null || targetVisible != null)) {
            warnings.add("Matched set includes " + rentedCount + " rented scooters.");
        }
        return warnings;
    }

    private static boolean isTypeMatch(String normalizedType, Scooter scooter) {
        if (scooter.getType() == null) {
            return false;
        }
        String normalized = scooter.getType().trim().toUpperCase(Locale.ROOT);
        if ("GEN3 PRO".equals(normalized) || "GEN3_PRO".equals(normalized) || "GEN-3-PRO".equals(normalized)) {
            normalized = "GEN3PRO";
        }
        return normalizedType.equals(normalized);
    }

    /**
     * 将滑板车实体转换为接口返回使用的 DTO。
     */
    private ScooterResponse mapToDTO(Scooter scooter) {
        BillingRule billingRule = billingService.getCurrentRule();
        String locName = (scooter.getLocationPoint() != null) ? scooter.getLocationPoint().getName() : "Unknown";
        return new ScooterResponse(
            scooter.getId(),
            scooter.getType(),
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
