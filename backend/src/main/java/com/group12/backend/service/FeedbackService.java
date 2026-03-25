package com.group12.backend.service;

import com.group12.backend.dto.FeedbackRequest;
import com.group12.backend.dto.UpdateFeedbackRequest;

/**
 * 定义用户反馈提交和反馈处理更新相关的服务能力。
 */
public interface FeedbackService {
    /**
     * 提交新的用户反馈。
     */
    Object submitFeedback(FeedbackRequest request);

    /**
     * 更新指定反馈的处理状态。
     */
    Object updateFeedback(String feedbackId, UpdateFeedbackRequest request);
}
