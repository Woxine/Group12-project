package com.group12.backend.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.group12.backend.security.AdminAccessGuard;
import com.group12.backend.service.AdminService;

import jakarta.servlet.http.HttpServletRequest;

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
}

