package com.group12.backend.service;

import com.group12.backend.dto.FeedbackRequest;
import com.group12.backend.dto.UpdateFeedbackRequest;

public interface FeedbackService {
    Object submitFeedback(FeedbackRequest request);
    Object updateFeedback(String feedbackId, UpdateFeedbackRequest request);
}
