package com.group12.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.group12.backend.entity.Scooter;
import com.group12.backend.repository.ScooterRepository;

/**
 * Repairs historical scooter rows that predate the GEN naming scheme.
 * The repair is intentionally narrow to avoid mutating already-correct data.
 */
@Service
public class LegacyScooterTypeRepairService {

    private static final Logger log = LoggerFactory.getLogger(LegacyScooterTypeRepairService.class);

    private static final List<Long> LEGACY_GEN1_IDS = List.of(
            1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L);

    private static final Set<String> LEGACY_GEN1_ALIASES = Set.of(
            "FZ3",
            "FZ-3",
            "NINEBOT FZ3",
            "NINEBOT-FZ3",
            "NINEBOT_FZ3",
            "GEN 1",
            "GEN-1",
            "GEN_1");

    private final ScooterRepository scooterRepository;

    public LegacyScooterTypeRepairService(ScooterRepository scooterRepository) {
        this.scooterRepository = scooterRepository;
    }

    @Transactional
    public void repairLegacyGen1Types() {
        List<Scooter> candidates = scooterRepository.findAllById(LEGACY_GEN1_IDS);
        List<Scooter> repairedScooters = new ArrayList<>();
        List<Long> repairedIds = new ArrayList<>();

        for (Scooter scooter : candidates) {
            if (!shouldRepairToGen1(scooter)) {
                continue;
            }
            scooter.setType("GEN1");
            repairedScooters.add(scooter);
            repairedIds.add(scooter.getId());
        }

        if (repairedScooters.isEmpty()) {
            log.info("Legacy scooter type repair completed with no changes");
            return;
        }

        scooterRepository.saveAll(repairedScooters);
        log.warn("Repaired legacy scooter type values to GEN1 for scooter IDs {}", repairedIds);
    }

    private boolean shouldRepairToGen1(Scooter scooter) {
        if (scooter == null || scooter.getId() == null || scooter.getId() < 1L || scooter.getId() > 12L) {
            return false;
        }

        String type = scooter.getType();
        if (type == null || type.isBlank()) {
            return true;
        }

        String normalized = type.trim().toUpperCase(Locale.ROOT);
        if ("GEN1".equals(normalized)) {
            return false;
        }

        return LEGACY_GEN1_ALIASES.contains(normalized);
    }
}
