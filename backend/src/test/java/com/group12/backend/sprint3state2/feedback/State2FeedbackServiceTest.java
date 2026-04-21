package com.group12.backend.sprint3state2.feedback;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.group12.backend.dto.ProcessFeedbackRequest;
import com.group12.backend.service.impl.FeedbackServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("Sprint3-State2 FeedbackService TODO")
class State2FeedbackServiceTest {

    @InjectMocks
    private FeedbackServiceImpl feedbackService;

    @Test
    @DisplayName("processFeedbackByPriority_todoThrows")
    void processFeedbackByPriority_todoThrows() {
        ProcessFeedbackRequest request = new ProcessFeedbackRequest();
        request.setAction("DIRECT_HANDLE");

        assertThatThrownBy(() -> feedbackService.processFeedbackByPriority("1", request, 100L))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("processFeedbackByPriority");
    }

    @Test
    @DisplayName("getHighPriorityIssues_todoThrows")
    void getHighPriorityIssues_todoThrows() {
        assertThatThrownBy(() -> feedbackService.getHighPriorityIssues(Boolean.TRUE, 1, 20))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("getHighPriorityIssues");
    }
}
