package com.group12.backend.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shared helpers for duration presets and minute-based booking time handling.
 */
public final class BookingTimeSupport {
    public static final int MIN_DURATION_MINUTES = 5;
    public static final int MAX_DURATION_MINUTES = 7 * 24 * 60;
    public static final int DURATION_STEP_MINUTES = 5;

    private static final DateTimeFormatter SPACE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter ISO_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private static final Map<String, Integer> PRESET_MINUTES = buildPresetMinutes();

    private BookingTimeSupport() {
    }

    private static Map<String, Integer> buildPresetMinutes() {
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("10M", 10);
        map.put("1H", 60);
        map.put("4H", 240);
        map.put("1D", 1440);
        map.put("1W", 10080);
        return map;
    }

    public static Integer presetMinutesByCode(String code) {
        if (code == null) return null;
        return PRESET_MINUTES.get(code.trim().toUpperCase());
    }

    public static String presetCodeByMinutes(int minutes) {
        for (Map.Entry<String, Integer> entry : PRESET_MINUTES.entrySet()) {
            if (entry.getValue() == minutes) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static int resolveDurationMinutes(Integer durationMinutes, String durationCode, String legacyDuration) {
        if (durationMinutes != null) {
            return durationMinutes;
        }
        Integer byCode = presetMinutesByCode(durationCode);
        if (byCode != null) {
            return byCode;
        }
        Integer byLegacy = presetMinutesByCode(legacyDuration);
        return byLegacy == null ? 60 : byLegacy;
    }

    public static void validateDurationMinutes(int durationMinutes) {
        if (durationMinutes < MIN_DURATION_MINUTES || durationMinutes > MAX_DURATION_MINUTES) {
            throw new IllegalArgumentException("durationMinutes must be between 5 and 10080");
        }
        if (durationMinutes % DURATION_STEP_MINUTES != 0) {
            throw new IllegalArgumentException("durationMinutes must be in 5-minute steps");
        }
    }

    public static LocalDateTime parseClientStartTime(String raw, LocalDateTime now) {
        if (raw == null || raw.isBlank()) {
            return now;
        }
        String value = raw.trim();
        LocalDateTime parsed;
        try {
            parsed = LocalDateTime.parse(value, SPACE_TIME);
        } catch (DateTimeParseException ex) {
            try {
                parsed = LocalDateTime.parse(value, ISO_TIME);
            } catch (DateTimeParseException ex2) {
                parsed = LocalDateTime.parse(value);
            }
        }
        LocalDate today = now.toLocalDate();
        if (!parsed.toLocalDate().equals(today)) {
            throw new IllegalArgumentException("startTime must be in the same day");
        }
        if (parsed.isBefore(now)) {
            throw new IllegalArgumentException("startTime cannot be earlier than current time");
        }
        return parsed;
    }

    public static String formatDurationLabel(int minutes) {
        String preset = presetCodeByMinutes(minutes);
        if (preset != null) {
            return preset;
        }
        int days = minutes / 1440;
        int remain = minutes % 1440;
        int hours = remain / 60;
        int mins = remain % 60;
        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("D");
        if (hours > 0) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(hours).append("H");
        }
        if (mins > 0 || sb.length() == 0) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(mins).append("M");
        }
        return sb.toString();
    }

    public static String toAnalyticsBucket(int minutes) {
        String preset = presetCodeByMinutes(minutes);
        return preset == null ? "CUSTOM" : preset;
    }
}
