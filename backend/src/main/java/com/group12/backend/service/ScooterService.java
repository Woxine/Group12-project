package com.group12.backend.service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 定义滑板车查询、定位和基础信息更新相关的服务能力。
 */
public interface ScooterService {
    /**
     * 查询滑板车列表，可按状态和分页条件筛选。
     */
    Map<String, Object> getScooters(String status, Integer page, Integer size);

    /**
     * 查询指定滑板车的位置信息。
     */
    Object getScooterLocation(String scooterId);

    /**
     * 更新指定滑板车的状态、计费和坐标信息。
     */
    Object updateScooter(Long id, String status, BigDecimal hourRate, Double locationLat, Double locationLng);
}
