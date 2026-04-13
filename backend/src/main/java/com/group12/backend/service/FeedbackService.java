package com.group12.backend.service;

import java.util.Map;

import com.group12.backend.dto.FeedbackRequest;
import com.group12.backend.dto.UpdateFeedbackRequest;

/**
 * 定义用户反馈提交和反馈处理更新相关的服务能力。
 */
public interface FeedbackService {
    /**
     * 提交新的用户反馈（关联当前登录用户）。
     */
    Object submitFeedback(Long userId, FeedbackRequest request);

    /**
     * 更新指定反馈的处理状态。
     */
    Object updateFeedback(String feedbackId, UpdateFeedbackRequest request);

    /**
     * 查询反馈列表，支持按处理状态、优先级和分页筛选。
     */
    Map<String, Object> getFeedbacks(Boolean resolved, String priority, Integer page, Integer size);
}
