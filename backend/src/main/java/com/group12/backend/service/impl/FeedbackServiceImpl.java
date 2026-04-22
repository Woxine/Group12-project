package com.group12.backend.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.Comparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.group12.backend.dto.FeedbackRequest;
import com.group12.backend.dto.FeedbackResponse;
import com.group12.backend.dto.EscalatedFeedbackResponse;
import com.group12.backend.dto.HighPriorityIssueItemDTO;
import com.group12.backend.dto.ProcessFeedbackRequest;
import com.group12.backend.dto.UpdateFeedbackRequest;
import com.group12.backend.entity.Feedback;
import com.group12.backend.entity.Scooter;
import com.group12.backend.entity.User;
import com.group12.backend.exception.BusinessException;
import com.group12.backend.exception.ErrorMessages;
import com.group12.backend.repository.FeedbackRepository;
import com.group12.backend.repository.ScooterRepository;
import com.group12.backend.repository.UserRepository;
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

    @Autowired
    private UserRepository userRepository;

    @Override
    /**
     * 保存新的反馈记录，并在可用时关联对应滑板车。
     */
    public Object submitFeedback(Long userId, FeedbackRequest request) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorMessages.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorMessages.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        Feedback feedback = new Feedback();
        feedback.setUser(user);
        feedback.setContent(request.getDescription());
        
        String reqPriority = request.getPriority();
        if (reqPriority == null || reqPriority.trim().isEmpty()) {
            feedback.setPriority("LOW");
        } else {
            String p = reqPriority.trim().toUpperCase();
            if (!p.equals("LOW") && !p.equals("HIGH")) {
                throw new BusinessException("Invalid priority value, must be LOW or HIGH", HttpStatus.BAD_REQUEST);
            }
            feedback.setPriority(p);
        }
        
        feedback.setResolved(false);
        feedback.setEscalated(false);
        feedback.setEscalationStatus("PENDING");

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

    @Override
    public Map<String, Object> getFeedbacks(Boolean resolved, String priority, Integer page, Integer size) {
        List<Feedback> feedbacks;

        if (resolved != null && priority != null && !priority.isBlank()) {
            feedbacks = feedbackRepository.findByResolvedAndPriorityIgnoreCase(resolved, priority);
        } else if (resolved != null) {
            feedbacks = feedbackRepository.findByResolved(resolved);
        } else if (priority != null && !priority.isBlank()) {
            feedbacks = feedbackRepository.findByPriorityIgnoreCase(priority);
        } else {
            feedbacks = feedbackRepository.findAll();
        }

        long total = feedbacks.size();
        if (page != null && size != null && page > 0 && size > 0) {
            int skip = (page - 1) * size;
            List<Object> data = feedbacks.stream()
                    .skip(skip)
                    .limit(size)
                    .map(this::mapToDTO)
                    .map(dto -> (Object) dto)
                    .toList();
            return Map.of("data", data, "total", total);
        }

        List<Object> data = feedbacks.stream()
                .map(this::mapToDTO)
                .map(dto -> (Object) dto)
                .toList();
        return Map.of("data", data, "total", total);
    }

    @Override
    public EscalatedFeedbackResponse processFeedbackByPriority(String feedbackId, ProcessFeedbackRequest request, Long operatorUserId) {
        Long id;
        try {
            id = Long.valueOf(feedbackId);
        } catch (NumberFormatException ex) {
            throw new BusinessException(ErrorMessages.INVALID_FEEDBACK_ID, HttpStatus.BAD_REQUEST);
        }

        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorMessages.feedbackNotFound(feedbackId), HttpStatus.NOT_FOUND));

        String action = request.getAction() == null ? "" : request.getAction().trim().toUpperCase();
        if (!"DIRECT_HANDLE".equals(action) && !"ESCALATE".equals(action)) {
            throw new BusinessException(ErrorMessages.INVALID_FEEDBACK_PROCESS_ACTION, HttpStatus.BAD_REQUEST);
        }

        String priority = feedback.getPriority() == null ? "LOW" : feedback.getPriority().trim().toUpperCase();
        String note = request.getNote() == null ? null : request.getNote().trim();
        if (note != null && note.isEmpty()) {
            note = null;
        }

        if ("HIGH".equals(priority)) {
            if (!"ESCALATE".equals(action)) {
                throw new BusinessException(ErrorMessages.HIGH_PRIORITY_REQUIRES_ESCALATION, HttpStatus.BAD_REQUEST);
            }
            if (Boolean.TRUE.equals(feedback.getEscalated())) {
                throw new BusinessException(ErrorMessages.FEEDBACK_ALREADY_ESCALATED, HttpStatus.CONFLICT);
            }
            String escalateTo = request.getEscalateTo() == null ? "" : request.getEscalateTo().trim();
            if (escalateTo.isEmpty()) {
                throw new BusinessException(ErrorMessages.ESCALATE_TARGET_REQUIRED, HttpStatus.BAD_REQUEST);
            }

            feedback.setEscalated(true);
            feedback.setEscalatedTo(escalateTo);
            feedback.setEscalatedAt(LocalDateTime.now());
            feedback.setEscalationStatus("ESCALATED");
            feedback.setResolved(false);
        } else {
            if (!"DIRECT_HANDLE".equals(action)) {
                throw new BusinessException(ErrorMessages.LOW_PRIORITY_DIRECT_HANDLE_ONLY, HttpStatus.BAD_REQUEST);
            }
            if (Boolean.TRUE.equals(feedback.getResolved())) {
                throw new BusinessException(ErrorMessages.FEEDBACK_ALREADY_RESOLVED, HttpStatus.CONFLICT);
            }

            feedback.setResolved(true);
            feedback.setEscalated(false);
            feedback.setEscalatedTo(null);
            feedback.setEscalatedAt(null);
            feedback.setEscalationStatus("DIRECT_HANDLED");
        }

        if (operatorUserId != null && operatorUserId > 0) {
            userRepository.findById(operatorUserId).ifPresent(feedback::setProcessedBy);
        }
        feedback.setProcessNote(note);

        Feedback saved = feedbackRepository.save(feedback);

        EscalatedFeedbackResponse response = new EscalatedFeedbackResponse();
        response.setFeedbackId(saved.getId());
        response.setPriority(saved.getPriority());
        response.setEscalated(saved.getEscalated());
        response.setEscalatedTo(saved.getEscalatedTo());
        response.setStatus(saved.getEscalationStatus());
        return response;
    }

    @Override
    public Map<String, Object> getHighPriorityIssues(Boolean escalated, Integer page, Integer size) {
        List<Feedback> highPriorityFeedbacks;
        if (escalated != null) {
            highPriorityFeedbacks = feedbackRepository.findByPriorityIgnoreCaseAndEscalated("HIGH", escalated);
        } else {
            highPriorityFeedbacks = feedbackRepository.findByPriorityIgnoreCase("HIGH");
        }

        List<Feedback> sortedFeedbacks = highPriorityFeedbacks.stream()
                .sorted(Comparator
                        .comparing(Feedback::getEscalatedAt, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(Feedback::getId, Comparator.reverseOrder()))
                .toList();

        long total = sortedFeedbacks.size();
        if (page != null && size != null && page > 0 && size > 0) {
            int skip = (page - 1) * size;
            List<Object> data = sortedFeedbacks.stream()
                    .skip(skip)
                    .limit(size)
                    .map(this::mapToHighPriorityIssueDTO)
                    .map(dto -> (Object) dto)
                    .toList();
            return Map.of("data", data, "total", total);
        }

        List<Object> data = sortedFeedbacks.stream()
                .map(this::mapToHighPriorityIssueDTO)
                .map(dto -> (Object) dto)
                .toList();
        return Map.of("data", data, "total", total);
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
            feedback.getResolved(),
            feedback.getEscalated(),
            feedback.getEscalatedTo(),
            feedback.getEscalationStatus()
        );
    }

    private HighPriorityIssueItemDTO mapToHighPriorityIssueDTO(Feedback feedback) {
        HighPriorityIssueItemDTO dto = new HighPriorityIssueItemDTO();
        dto.setFeedbackId(feedback.getId());
        dto.setUserId(feedback.getUser() != null ? feedback.getUser().getId() : null);
        dto.setScooterId(feedback.getScooter() != null ? feedback.getScooter().getId() : null);
        dto.setContent(feedback.getContent());
        dto.setPriority(feedback.getPriority());
        dto.setEscalated(feedback.getEscalated());
        dto.setEscalatedTo(feedback.getEscalatedTo());
        dto.setResolved(feedback.getResolved());
        return dto;
    }
}
