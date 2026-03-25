package com.group12.backend.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.group12.backend.dto.FeedbackRequest;
import com.group12.backend.dto.FeedbackResponse;
import com.group12.backend.dto.UpdateFeedbackRequest;
import com.group12.backend.entity.Feedback;
import com.group12.backend.entity.Scooter;
import com.group12.backend.exception.BusinessException;
import com.group12.backend.exception.ErrorMessages;
import com.group12.backend.repository.FeedbackRepository;
import com.group12.backend.repository.ScooterRepository;
import com.group12.backend.service.FeedbackService;

/**
 * 实现用户反馈提交、反馈状态更新和结果转换相关的业务逻辑。
 */
@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private ScooterRepository scooterRepository;

    @Override
    /**
     * 保存新的反馈记录，并在可用时关联对应滑板车。
     */
    public Object submitFeedback(FeedbackRequest request) {
        Feedback feedback = new Feedback();
        feedback.setContent(request.getDescription());
        feedback.setPriority("LOW");
        feedback.setResolved(false);

        if (request.getScooter_id() != null && !request.getScooter_id().isEmpty()) {
            try {
                Long scooterId = Long.valueOf(request.getScooter_id());
                Optional<Scooter> scooterOpt = scooterRepository.findById(scooterId);
                scooterOpt.ifPresent(feedback::setScooter);
            } catch (NumberFormatException e) {
                // ignore invalid scooter id format
            }
        }

        Feedback saved = feedbackRepository.save(feedback);
        return mapToDTO(saved);
    }

    @Override
    /**
     * 更新指定反馈的处理状态并返回最新结果。
     */
    public Object updateFeedback(String feedbackId, UpdateFeedbackRequest request) {
        Long id = Long.valueOf(feedbackId);
        Optional<Feedback> feedbackOpt = feedbackRepository.findById(id);

        if (feedbackOpt.isPresent()) {
            Feedback feedback = feedbackOpt.get();
            if (request.getStatus() != null) {
                boolean isResolved = "resolved".equalsIgnoreCase(request.getStatus()) 
                                  || "completed".equalsIgnoreCase(request.getStatus())
                                  || "true".equalsIgnoreCase(request.getStatus());
                feedback.setResolved(isResolved);
            }
            
            Feedback saved = feedbackRepository.save(feedback);
            return mapToDTO(saved);
        } else {
            throw new BusinessException(ErrorMessages.feedbackNotFound(feedbackId), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 将反馈实体转换为前端返回使用的 DTO。
     */
    private FeedbackResponse mapToDTO(Feedback feedback) {
        return new FeedbackResponse(
            feedback.getId(),
            (feedback.getUser() != null) ? feedback.getUser().getId() : null,
            (feedback.getScooter() != null) ? feedback.getScooter().getId() : null,
            feedback.getContent(),
            feedback.getPriority(),
            feedback.getResolved()
        );
    }
}
