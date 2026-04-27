package com.group12.backend.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.group12.backend.dto.BillingSettingsResponse;
import com.group12.backend.dto.BillingSettingsLogResponse;
import com.group12.backend.dto.BulkScooterUpdateRequest;
import com.group12.backend.dto.CreateScooterRequest;
import com.group12.backend.dto.UpdateBillingSettingsRequest;
import com.group12.backend.entity.BillingSettingsLog;
import com.group12.backend.security.AdminAccessGuard;
import com.group12.backend.service.BillingRule;
import com.group12.backend.service.BillingService;
import com.group12.backend.service.AdminService;
import com.group12.backend.service.ScooterService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * 负责提供后台管理端的营收统计与经营分析接口。
 */
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private AdminAccessGuard adminAccessGuard;

    @Autowired
    private BillingService billingService;

    @Autowired
    private ScooterService scooterService;

    /**
     * 管理端滑板车列表（含已从客户端隐藏的车辆）。
     */
    @GetMapping("/scooters")
    public ResponseEntity<Object> getScootersForAdmin(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Integer limit,
            HttpServletRequest request) {
        adminAccessGuard.requireAdmin(request);
        Integer finalSize = size != null ? size : limit;
        return ResponseEntity.ok(scooterService.getScootersForAdmin(status, page, finalSize));
    }

    /**
     * 新增滑板车。
     */
    @PostMapping("/scooters")
    public ResponseEntity<Object> createScooter(
            @Valid @RequestBody CreateScooterRequest body,
            HttpServletRequest request) {
        adminAccessGuard.requireAdmin(request);
        return ResponseEntity.ok(Map.of("data", scooterService.createScooter(body)));
    }

    /**
     * 删除滑板车（存在订单记录时不允许删除）。
     */
    @DeleteMapping("/scooters/{scooterId}")
    public ResponseEntity<Object> deleteScooter(@PathVariable Long scooterId, HttpServletRequest request) {
        adminAccessGuard.requireAdmin(request);
        scooterService.deleteScooter(scooterId);
        return ResponseEntity.ok(Map.of("message", "Scooter deleted"));
    }

    /**
     * 预览按车型批量修改将影响的车辆规模与风险。
     */
    @PostMapping("/scooters/bulk-by-type/preview")
    public ResponseEntity<Object> previewBulkUpdateByType(
            @Valid @RequestBody BulkScooterUpdateRequest body,
            HttpServletRequest request) {
        adminAccessGuard.requireAdmin(request);
        return ResponseEntity.ok(Map.of("data", scooterService.previewBulkUpdateByType(body)));
    }

    /**
     * 按车型批量更新车辆参数（高风险字段需显式确认）。
     */
    @PostMapping("/scooters/bulk-by-type/apply")
    public ResponseEntity<Object> applyBulkUpdateByType(
            @Valid @RequestBody BulkScooterUpdateRequest body,
            HttpServletRequest request) {
        adminAccessGuard.requireAdmin(request);
        return ResponseEntity.ok(Map.of("data", scooterService.applyBulkUpdateByType(body)));
    }

    // API-008: 获取收入统计信息
    /**
     * 获取收益统计信息
     * 根据起始日期和结束日期查询收入数据
     */
    @GetMapping("/revenue")
    public ResponseEntity<Object> getRevenueStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start_date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end_date,
            HttpServletRequest request) {
        adminAccessGuard.requireAdmin(request);
        return ResponseEntity.ok(Map.of("data", adminService.getRevenueStatistics(start_date, end_date)));
    }

    /**
     * 获取指定日期范围内按租期分类汇总的收入统计结果。
     */
    @GetMapping("/revenue/duration")
    public ResponseEntity<Object> getRevenueByDuration(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start_date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end_date,
            HttpServletRequest request) {
        adminAccessGuard.requireAdmin(request);
        return ResponseEntity.ok(Map.of("data", adminService.getRevenueByDuration(start_date, end_date)));
    }

    /**
     * 按租期分类的本周收入统计
     */
    @GetMapping("/revenue/duration-week")
    public ResponseEntity<Object> getWeeklyRevenueByDuration(HttpServletRequest request) {
        adminAccessGuard.requireAdmin(request);
        return ResponseEntity.ok(Map.of("data", adminService.getWeeklyRevenueByDuration()));
    }

    /**
     * ID20 TODO: 本周热门租赁日期周榜。
     */
    @GetMapping("/revenue/popular-dates-week")
    public ResponseEntity<Object> getPopularRentalDatesThisWeek(HttpServletRequest request) {
        adminAccessGuard.requireAdmin(request);
        return ResponseEntity.ok(Map.of("data", adminService.getPopularRentalDatesThisWeek()));
    }

    /**
     * 获取指定日期范围热门租赁日期榜单。
     */
    @GetMapping("/revenue/popular-dates")
    public ResponseEntity<Object> getPopularRentalDates(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start_date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end_date,
            HttpServletRequest request) {
        adminAccessGuard.requireAdmin(request);
        return ResponseEntity.ok(Map.of("data", adminService.getPopularRentalDates(start_date, end_date)));
    }

    /**
     * 获取管理端看板总览数据。
     */
    @GetMapping("/dashboard/overview")
    public ResponseEntity<Object> getDashboardOverview(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start_date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end_date,
            HttpServletRequest request) {
        adminAccessGuard.requireAdmin(request);
        return ResponseEntity.ok(Map.of("data", adminService.getDashboardOverview(start_date, end_date)));
    }

    @GetMapping("/billing-settings")
    public ResponseEntity<Object> getBillingSettings(HttpServletRequest request) {
        adminAccessGuard.requireAdmin(request);
        BillingRule rule = billingService.getCurrentRule();
        return ResponseEntity.ok(Map.of("data", toBillingSettingsResponse(rule)));
    }

    @PutMapping("/billing-settings")
    public ResponseEntity<Object> updateBillingSettings(
            @Valid @RequestBody UpdateBillingSettingsRequest updateRequest,
            HttpServletRequest request) {
        adminAccessGuard.requireAdmin(request);
        Long operatorUserId = extractUserId(request);
        BillingRule updated = billingService.updateSettings(
                toBigDecimal(updateRequest.getLongRentHourRateMultiplier()),
                toBigDecimal(updateRequest.getExtraLongRentHourRateMultiplier()),
                toBigDecimal(updateRequest.getStudentDiscountRate()),
                toBigDecimal(updateRequest.getSeniorDiscountRate()),
                toBigDecimal(updateRequest.getFrequentDiscountRate()),
                operatorUserId);
        return ResponseEntity.ok(Map.of("data", toBillingSettingsResponse(updated)));
    }

    @GetMapping("/billing-settings/logs")
    public ResponseEntity<Object> getBillingSettingsLogs(
            @RequestParam(required = false, defaultValue = "20") Integer limit,
            HttpServletRequest request) {
        adminAccessGuard.requireAdmin(request);
        List<BillingSettingsLog> logs = billingService.getRecentLogs(limit == null ? 20 : limit);
        List<Object> data = logs.stream().map(AdminController::toBillingSettingsLogResponse).map(o -> (Object) o).toList();
        return ResponseEntity.ok(Map.of("data", data, "total", data.size()));
    }

    private static BillingSettingsResponse toBillingSettingsResponse(BillingRule rule) {
        BillingSettingsResponse response = new BillingSettingsResponse();
        response.setLongRentThresholdHours(
                rule.longRentThresholdHours() == null ? null : rule.longRentThresholdHours().doubleValue());
        response.setExtraLongRentThresholdHours(
                rule.extraLongRentThresholdHours() == null ? null : rule.extraLongRentThresholdHours().doubleValue());
        response.setLongRentHourRateMultiplier(
                rule.longRentHourRateMultiplier() == null ? null : rule.longRentHourRateMultiplier().doubleValue());
        response.setExtraLongRentHourRateMultiplier(
                rule.extraLongRentHourRateMultiplier() == null ? null : rule.extraLongRentHourRateMultiplier().doubleValue());
        response.setStudentDiscountRate(
                rule.studentDiscountRate() == null ? null : rule.studentDiscountRate().doubleValue());
        response.setSeniorDiscountRate(
                rule.seniorDiscountRate() == null ? null : rule.seniorDiscountRate().doubleValue());
        response.setFrequentDiscountRate(
                rule.frequentDiscountRate() == null ? null : rule.frequentDiscountRate().doubleValue());
        response.setUpdatedAt(rule.updatedAt());
        return response;
    }

    private static BillingSettingsLogResponse toBillingSettingsLogResponse(BillingSettingsLog log) {
        BillingSettingsLogResponse response = new BillingSettingsLogResponse();
        response.setId(log.getId());
        response.setOldLongRentHourRateMultiplier(
                log.getOldLongRentMultiplier() == null ? null : log.getOldLongRentMultiplier().doubleValue());
        response.setNewLongRentHourRateMultiplier(
                log.getNewLongRentMultiplier() == null ? null : log.getNewLongRentMultiplier().doubleValue());
        response.setOldExtraLongRentHourRateMultiplier(
                log.getOldExtraLongRentMultiplier() == null ? null : log.getOldExtraLongRentMultiplier().doubleValue());
        response.setNewExtraLongRentHourRateMultiplier(
                log.getNewExtraLongRentMultiplier() == null ? null : log.getNewExtraLongRentMultiplier().doubleValue());
        response.setOperatorUserId(log.getOperatorUserId());
        response.setCreatedAt(log.getCreatedAt());
        return response;
    }

    private static Long extractUserId(HttpServletRequest request) {
        Object authUserId = request.getAttribute("userId");
        if (authUserId == null) return null;
        try {
            return Long.valueOf(String.valueOf(authUserId));
        } catch (Exception ignored) {
            return null;
        }
    }

    private static java.math.BigDecimal toBigDecimal(Double value) {
        return value == null ? null : java.math.BigDecimal.valueOf(value);
    }
}

