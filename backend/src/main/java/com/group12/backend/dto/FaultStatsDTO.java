package com.group12.backend.dto;

import java.util.Map;

/**
 * 用于展示故障反馈总量、处理状态与优先级分布。
 */
public class FaultStatsDTO {
    private Integer totalFeedbacks;
    private Integer resolvedFeedbacks;
    private Integer unresolvedFeedbacks;
    private Map<String, Integer> priorityDistribution;

    public FaultStatsDTO(
            Integer totalFeedbacks,
            Integer resolvedFeedbacks,
            Integer unresolvedFeedbacks,
            Map<String, Integer> priorityDistribution) {
        this.totalFeedbacks = totalFeedbacks;
        this.resolvedFeedbacks = resolvedFeedbacks;
        this.unresolvedFeedbacks = unresolvedFeedbacks;
        this.priorityDistribution = priorityDistribution;
    }

    public Integer getTotalFeedbacks() { return totalFeedbacks; }
    public void setTotalFeedbacks(Integer totalFeedbacks) { this.totalFeedbacks = totalFeedbacks; }

    public Integer getResolvedFeedbacks() { return resolvedFeedbacks; }
    public void setResolvedFeedbacks(Integer resolvedFeedbacks) { this.resolvedFeedbacks = resolvedFeedbacks; }

    public Integer getUnresolvedFeedbacks() { return unresolvedFeedbacks; }
    public void setUnresolvedFeedbacks(Integer unresolvedFeedbacks) { this.unresolvedFeedbacks = unresolvedFeedbacks; }

    public Map<String, Integer> getPriorityDistribution() { return priorityDistribution; }
    public void setPriorityDistribution(Map<String, Integer> priorityDistribution) {
        this.priorityDistribution = priorityDistribution;
    }
}
