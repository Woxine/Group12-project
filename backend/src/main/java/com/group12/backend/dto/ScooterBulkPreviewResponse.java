package com.group12.backend.dto;

import java.util.List;
import java.util.Map;

/**
 * Preview response for bulk-by-type scooter updates.
 */
public class ScooterBulkPreviewResponse {

    private String type;
    private int matchedCount;
    private int hiddenCount;
    private Map<String, Long> statusBreakdown;
    private boolean risky;
    private List<String> riskWarnings;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getMatchedCount() {
        return matchedCount;
    }

    public void setMatchedCount(int matchedCount) {
        this.matchedCount = matchedCount;
    }

    public int getHiddenCount() {
        return hiddenCount;
    }

    public void setHiddenCount(int hiddenCount) {
        this.hiddenCount = hiddenCount;
    }

    public Map<String, Long> getStatusBreakdown() {
        return statusBreakdown;
    }

    public void setStatusBreakdown(Map<String, Long> statusBreakdown) {
        this.statusBreakdown = statusBreakdown;
    }

    public boolean isRisky() {
        return risky;
    }

    public void setRisky(boolean risky) {
        this.risky = risky;
    }

    public List<String> getRiskWarnings() {
        return riskWarnings;
    }

    public void setRiskWarnings(List<String> riskWarnings) {
        this.riskWarnings = riskWarnings;
    }
}
