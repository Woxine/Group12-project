package com.group12.backend.service;

import java.math.BigDecimal;
import java.util.Map;

import com.group12.backend.dto.BulkScooterUpdateRequest;
import com.group12.backend.dto.CreateScooterRequest;
import com.group12.backend.dto.ScooterBulkApplyResponse;
import com.group12.backend.dto.ScooterBulkPreviewResponse;

/**
 * 定义滑板车查询、定位和基础信息更新相关的服务能力。
 */
public interface ScooterService {
    /**
     * 查询滑板车列表，可按状态和分页条件筛选（仅对客户端展示可见车辆）。
     */
    Map<String, Object> getScooters(String status, Integer page, Integer size);

    /**
     * 管理端列表：包含已从客户端隐藏的车辆。
     */
    Map<String, Object> getScootersForAdmin(String status, Integer page, Integer size);

    /**
     * 查询指定滑板车的位置信息。
     */
    Object getScooterLocation(String scooterId);

    /**
     * 更新指定滑板车的状态、计费和坐标信息。
     */
    Object updateScooter(Long id, String type, String status, BigDecimal hourRate, Double locationLat, Double locationLng, Boolean visible);

    /**
     * 管理员新增车辆。
     */
    Object createScooter(CreateScooterRequest request);

    /**
     * 预览按车型批量更新将影响的车辆范围与风险信息。
     */
    ScooterBulkPreviewResponse previewBulkUpdateByType(BulkScooterUpdateRequest request);

    /**
     * 按车型批量更新车辆参数（需显式确认高风险字段）。
     */
    ScooterBulkApplyResponse applyBulkUpdateByType(BulkScooterUpdateRequest request);

    /**
     * 永久删除车辆（存在任意订单记录时不允许删除）。
     */
    void deleteScooter(Long id);
}
