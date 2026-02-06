package com.group12.backend.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.group12.backend.dto.FeedbackRequest;
import com.group12.backend.dto.UpdateFeedbackRequest;
import com.group12.backend.entity.Feedback;
import com.group12.backend.entity.Scooter;
import com.group12.backend.repository.FeedbackRepository;
import com.group12.backend.repository.ScooterRepository;
import com.group12.backend.service.FeedbackService;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private ScooterRepository scooterRepository;

    @Override
    public Object submitFeedback(FeedbackRequest request) {
        Feedback feedback = new Feedback();
        feedback.setContent(request.getDescription());
        
        // Default priority
        feedback.setPriority("LOW");
        feedback.setResolved(false);

        // Link scooter if provided
        if (request.getScooter_id() != null && !request.getScooter_id().isEmpty()) {
            try {
                Long scooterId = Long.valueOf(request.getScooter_id());
                Optional<Scooter> scooterOpt = scooterRepository.findById(scooterId);
                scooterOpt.ifPresent(feedback::setScooter);
            } catch (NumberFormatException e) {
                // Ignore invalid format, log it if logging was set up
            }
        }

        // Note: User is not set because we don't have user info in request currently.
        // Ideally we would get it from SecurityContext.

        return feedbackRepository.save(feedback);
    }

    @Override
    public Object updateFeedback(String feedbackId, UpdateFeedbackRequest request) {
        Long id = Long.valueOf(feedbackId);
        Optional<Feedback> feedbackOpt = feedbackRepository.findById(id);

        if (feedbackOpt.isPresent()) {
            Feedback feedback = feedbackOpt.get();
            if (request.getStatus() != null) {
                // Simple logic: "resolved" or "completed" -> true
                boolean isResolved = "resolved".equalsIgnoreCase(request.getStatus()) 
                                  || "completed".equalsIgnoreCase(request.getStatus())
                                  || "true".equalsIgnoreCase(request.getStatus());
                feedback.setResolved(isResolved);
            }
            // If there was a 'note' field in Feedback entity, we would update it here.
            // But Feedback entity currently doesn't have a 'note' field based on my read.
            // So we just save the status.
            
            return feedbackRepository.save(feedback);
        } else {
            throw new RuntimeException("Feedback not found with id: " + feedbackId);
        }
    }
}
