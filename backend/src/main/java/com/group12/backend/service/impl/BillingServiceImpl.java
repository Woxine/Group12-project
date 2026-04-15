package com.group12.backend.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.group12.backend.config.BillingProperties;
import com.group12.backend.entity.BillingSettings;
import com.group12.backend.entity.BillingSettingsLog;
import com.group12.backend.exception.BusinessException;
import com.group12.backend.repository.BillingSettingsLogRepository;
import com.group12.backend.repository.BillingSettingsRepository;
import com.group12.backend.service.BillingRule;
import com.group12.backend.service.BillingService;

@Service
public class BillingServiceImpl implements BillingService {
    private static final Long SINGLETON_ID = 1L;
    private static final BigDecimal MIN_POSITIVE = new BigDecimal("0.0001");
    private static final BigDecimal MAX_MULTIPLIER = BigDecimal.ONE;

    @Autowired
    private BillingSettingsRepository billingSettingsRepository;

    @Autowired
    private BillingProperties billingProperties;

    @Autowired
    private BillingSettingsLogRepository billingSettingsLogRepository;

    @Override
    @Transactional
    public BillingRule getCurrentRule() {
        BillingSettings settings = loadOrCreateSettings();
        return toRule(settings);
    }

    @Override
    @Transactional
    public BillingRule updateMultipliers(BigDecimal longRentHourRateMultiplier, BigDecimal extraLongRentHourRateMultiplier, Long operatorUserId) {
        validateMultiplier(longRentHourRateMultiplier, "longRentHourRateMultiplier");
        validateMultiplier(extraLongRentHourRateMultiplier, "extraLongRentHourRateMultiplier");
        if (extraLongRentHourRateMultiplier.compareTo(longRentHourRateMultiplier) > 0) {
            throw new BusinessException("extraLongRentHourRateMultiplier must be <= longRentHourRateMultiplier", HttpStatus.BAD_REQUEST);
        }
        BillingSettings settings = loadOrCreateSettings();
        BigDecimal oldM1 = settings.getLongRentMultiplier();
        BigDecimal oldM2 = settings.getExtraLongRentMultiplier();
        settings.setLongRentMultiplier(longRentHourRateMultiplier);
        settings.setExtraLongRentMultiplier(extraLongRentHourRateMultiplier);
        settings.setUpdatedAt(LocalDateTime.now());
        BillingSettings saved = billingSettingsRepository.save(settings);
        BillingSettingsLog log = new BillingSettingsLog();
        log.setOldLongRentMultiplier(oldM1);
        log.setNewLongRentMultiplier(longRentHourRateMultiplier);
        log.setOldExtraLongRentMultiplier(oldM2);
        log.setNewExtraLongRentMultiplier(extraLongRentHourRateMultiplier);
        log.setOperatorUserId(operatorUserId);
        billingSettingsLogRepository.save(log);
        return toRule(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BillingSettingsLog> getRecentLogs(int limit) {
        int safeLimit = limit <= 0 ? 20 : Math.min(limit, 100);
        return billingSettingsLogRepository.findAll(
                PageRequest.of(0, safeLimit, Sort.by(Sort.Direction.DESC, "createdAt"))).getContent();
    }

    private BillingSettings loadOrCreateSettings() {
        return billingSettingsRepository.findById(SINGLETON_ID).orElseGet(this::createDefaultSettings);
    }

    private BillingSettings createDefaultSettings() {
        BigDecimal t1 = defaultValue(billingProperties.getLongRentThresholdHours(), new BigDecimal("24"));
        BigDecimal t2 = defaultValue(billingProperties.getExtraLongRentThresholdHours(), new BigDecimal("72"));
        if (t2.compareTo(t1) <= 0) {
            throw new BusinessException("billing thresholds must satisfy extraLongRentThresholdHours > longRentThresholdHours", HttpStatus.BAD_REQUEST);
        }
        BigDecimal m1 = defaultValue(billingProperties.getLongRentHourRateMultiplier(), new BigDecimal("0.85"));
        BigDecimal m2 = defaultValue(billingProperties.getExtraLongRentHourRateMultiplier(), new BigDecimal("0.75"));
        validateMultiplier(m1, "longRentHourRateMultiplier");
        validateMultiplier(m2, "extraLongRentHourRateMultiplier");
        if (m2.compareTo(m1) > 0) {
            throw new BusinessException("extraLongRentHourRateMultiplier must be <= longRentHourRateMultiplier", HttpStatus.BAD_REQUEST);
        }

        BillingSettings defaults = new BillingSettings();
        defaults.setId(SINGLETON_ID);
        defaults.setLongRentThresholdHours(t1);
        defaults.setExtraLongRentThresholdHours(t2);
        defaults.setLongRentMultiplier(m1);
        defaults.setExtraLongRentMultiplier(m2);
        defaults.setUpdatedAt(LocalDateTime.now());
        return billingSettingsRepository.save(defaults);
    }

    private static void validateMultiplier(BigDecimal value, String fieldName) {
        if (value == null) {
            throw new BusinessException(fieldName + " is required", HttpStatus.BAD_REQUEST);
        }
        if (value.compareTo(MIN_POSITIVE) < 0 || value.compareTo(MAX_MULTIPLIER) > 0) {
            throw new BusinessException(fieldName + " must be in range [0.0001, 1.0]", HttpStatus.BAD_REQUEST);
        }
    }

    private static BigDecimal defaultValue(BigDecimal candidate, BigDecimal fallback) {
        return candidate == null ? fallback : candidate;
    }

    private static BillingRule toRule(BillingSettings settings) {
        return new BillingRule(
                settings.getLongRentThresholdHours(),
                settings.getExtraLongRentThresholdHours(),
                settings.getLongRentMultiplier(),
                settings.getExtraLongRentMultiplier(),
                settings.getUpdatedAt());
    }
}
