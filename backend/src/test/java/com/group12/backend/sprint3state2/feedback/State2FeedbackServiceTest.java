package com.group12.backend.sprint3state2.feedback;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.group12.backend.dto.EscalatedFeedbackResponse;
import com.group12.backend.dto.HighPriorityIssueItemDTO;
import com.group12.backend.dto.ProcessFeedbackRequest;
import com.group12.backend.entity.Feedback;
import com.group12.backend.entity.User;
import com.group12.backend.exception.BusinessException;
import com.group12.backend.exception.ErrorMessages;
import com.group12.backend.repository.FeedbackRepository;
import com.group12.backend.repository.ScooterRepository;
import com.group12.backend.repository.UserRepository;
import com.group12.backend.service.impl.FeedbackServiceImpl;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("Sprint3-State2 FeedbackService")
class State2FeedbackServiceTest {

    @Mock
    private FeedbackRepository feedbackRepository;
    @Mock
    private ScooterRepository scooterRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FeedbackServiceImpl feedbackService;

    @Test
    @DisplayName("processFeedbackByPriority_lowDirectHandle_success")
    void processFeedbackByPriority_lowDirectHandle_success() {
        Feedback feedback = new Feedback();
        feedback.setId(1L);
        feedback.setPriority("LOW");
        feedback.setResolved(false);
        feedback.setEscalated(false);

        User operator = new User();
        operator.setId(100L);

        ProcessFeedbackRequest request = new ProcessFeedbackRequest();
        request.setAction("DIRECT_HANDLE");
        request.setNote("Handled by frontline");

        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(feedback));
        when(userRepository.findById(100L)).thenReturn(Optional.of(operator));
        when(feedbackRepository.save(any(Feedback.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EscalatedFeedbackResponse response = feedbackService.processFeedbackByPriority("1", request, 100L);

        assertThat(response.getFeedbackId()).isEqualTo(1L);
        assertThat(response.getEscalated()).isFalse();
        assertThat(response.getStatus()).isEqualTo("DIRECT_HANDLED");
        assertThat(feedback.getResolved()).isTrue();
        assertThat(feedback.getEscalationStatus()).isEqualTo("DIRECT_HANDLED");
        assertThat(feedback.getProcessedBy()).isNotNull();
        assertThat(feedback.getProcessNote()).isEqualTo("Handled by frontline");
    }

    @Test
    @DisplayName("processFeedbackByPriority_highEscalate_success")
    void processFeedbackByPriority_highEscalate_success() {
        Feedback feedback = new Feedback();
        feedback.setId(2L);
        feedback.setPriority("HIGH");
        feedback.setResolved(false);
        feedback.setEscalated(false);

        User operator = new User();
        operator.setId(101L);

        ProcessFeedbackRequest request = new ProcessFeedbackRequest();
        request.setAction("ESCALATE");
        request.setEscalateTo("TECH_TEAM");
        request.setNote("Need professional support");

        when(feedbackRepository.findById(2L)).thenReturn(Optional.of(feedback));
        when(userRepository.findById(101L)).thenReturn(Optional.of(operator));
        when(feedbackRepository.save(any(Feedback.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EscalatedFeedbackResponse response = feedbackService.processFeedbackByPriority("2", request, 101L);

        assertThat(response.getFeedbackId()).isEqualTo(2L);
        assertThat(response.getEscalated()).isTrue();
        assertThat(response.getEscalatedTo()).isEqualTo("TECH_TEAM");
        assertThat(response.getStatus()).isEqualTo("ESCALATED");
        assertThat(feedback.getEscalatedAt()).isNotNull();
        assertThat(feedback.getEscalationStatus()).isEqualTo("ESCALATED");
        assertThat(feedback.getResolved()).isFalse();
    }

    @Test
    @DisplayName("processFeedbackByPriority_highDirectHandle_rejected")
    void processFeedbackByPriority_highDirectHandle_rejected() {
        Feedback feedback = new Feedback();
        feedback.setId(3L);
        feedback.setPriority("HIGH");

        ProcessFeedbackRequest request = new ProcessFeedbackRequest();
        request.setAction("DIRECT_HANDLE");

        when(feedbackRepository.findById(3L)).thenReturn(Optional.of(feedback));

        assertThatThrownBy(() -> feedbackService.processFeedbackByPriority("3", request, 100L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorMessages.HIGH_PRIORITY_REQUIRES_ESCALATION)
                .extracting(ex -> ((BusinessException) ex).getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("processFeedbackByPriority_invalidFeedbackId_rejected")
    void processFeedbackByPriority_invalidFeedbackId_rejected() {
        ProcessFeedbackRequest request = new ProcessFeedbackRequest();
        request.setAction("DIRECT_HANDLE");

        assertThatThrownBy(() -> feedbackService.processFeedbackByPriority("abc", request, 100L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorMessages.INVALID_FEEDBACK_ID);
    }

    @Test
    @DisplayName("getHighPriorityIssues_withEscalatedFilter_sortedAndPaged")
    void getHighPriorityIssues_withEscalatedFilter_sortedAndPaged() {
        Feedback olderEscalated = new Feedback();
        olderEscalated.setId(10L);
        olderEscalated.setPriority("HIGH");
        olderEscalated.setEscalated(true);
        olderEscalated.setEscalatedTo("TECH_TEAM");
        olderEscalated.setEscalatedAt(LocalDateTime.now().minusHours(1));
        olderEscalated.setResolved(false);
        olderEscalated.setContent("Battery issue");

        Feedback newerEscalated = new Feedback();
        newerEscalated.setId(11L);
        newerEscalated.setPriority("HIGH");
        newerEscalated.setEscalated(true);
        newerEscalated.setEscalatedTo("MAINTENANCE_TEAM");
        newerEscalated.setEscalatedAt(LocalDateTime.now());
        newerEscalated.setResolved(false);
        newerEscalated.setContent("Brake failure");

        when(feedbackRepository.findByPriorityIgnoreCaseAndEscalated("HIGH", true))
                .thenReturn(List.of(olderEscalated, newerEscalated));

        Map<String, Object> result = feedbackService.getHighPriorityIssues(Boolean.TRUE, 1, 1);

        assertThat(result.get("total")).isEqualTo(2L);
        List<?> data = (List<?>) result.get("data");
        assertThat(data).hasSize(1);
        HighPriorityIssueItemDTO first = (HighPriorityIssueItemDTO) data.get(0);
        assertThat(first.getFeedbackId()).isEqualTo(11L);
        assertThat(first.getEscalated()).isTrue();
        assertThat(first.getEscalatedTo()).isEqualTo("MAINTENANCE_TEAM");
    }

    @Test
    @DisplayName("getHighPriorityIssues_withoutEscalatedFilter_returnsAllHighPriority")
    void getHighPriorityIssues_withoutEscalatedFilter_returnsAllHighPriority() {
        Feedback highNotEscalated = new Feedback();
        highNotEscalated.setId(21L);
        highNotEscalated.setPriority("HIGH");
        highNotEscalated.setEscalated(false);
        highNotEscalated.setEscalatedTo(null);
        highNotEscalated.setEscalatedAt(null);
        highNotEscalated.setResolved(false);
        highNotEscalated.setContent("App issue");

        Feedback highEscalated = new Feedback();
        highEscalated.setId(22L);
        highEscalated.setPriority("HIGH");
        highEscalated.setEscalated(true);
        highEscalated.setEscalatedTo("TECH_TEAM");
        highEscalated.setEscalatedAt(LocalDateTime.now().minusMinutes(5));
        highEscalated.setResolved(false);
        highEscalated.setContent("Wheel issue");

        when(feedbackRepository.findByPriorityIgnoreCase("HIGH"))
                .thenReturn(List.of(highNotEscalated, highEscalated));

        Map<String, Object> result = feedbackService.getHighPriorityIssues(null, null, null);

        assertThat(result.get("total")).isEqualTo(2L);
        List<?> data = (List<?>) result.get("data");
        assertThat(data).hasSize(2);
        HighPriorityIssueItemDTO first = (HighPriorityIssueItemDTO) data.get(0);
        HighPriorityIssueItemDTO second = (HighPriorityIssueItemDTO) data.get(1);
        assertThat(first.getFeedbackId()).isEqualTo(22L);
        assertThat(second.getFeedbackId()).isEqualTo(21L);
        assertThat(second.getEscalated()).isFalse();
    }
}
