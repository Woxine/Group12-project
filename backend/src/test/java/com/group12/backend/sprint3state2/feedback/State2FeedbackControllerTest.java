package com.group12.backend.sprint3state2.feedback;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

import com.group12.backend.controller.FeedbackController;
import com.group12.backend.dto.EscalatedFeedbackResponse;
import com.group12.backend.dto.ProcessFeedbackRequest;
import com.group12.backend.security.AdminAccessGuard;
import com.group12.backend.service.FeedbackService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Sprint3-State2 FeedbackController")
class State2FeedbackControllerTest {

    @Mock
    private FeedbackService feedbackService;
    @Mock
    private AdminAccessGuard adminAccessGuard;

    private FeedbackController controller;

    @BeforeEach
    void setUp() {
        controller = new FeedbackController();
        ReflectionTestUtils.setField(controller, "feedbackService", feedbackService);
        ReflectionTestUtils.setField(controller, "adminAccessGuard", adminAccessGuard);
    }

    @Test
    @DisplayName("processByPriority_callsServiceAndReturnsData")
    void processByPriority_callsServiceAndReturnsData() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("userId", 100L);

        ProcessFeedbackRequest payload = new ProcessFeedbackRequest();
        payload.setAction("ESCALATE");
        payload.setEscalateTo("TECH_TEAM");

        EscalatedFeedbackResponse serviceResult = new EscalatedFeedbackResponse();
        serviceResult.setFeedbackId(88L);
        when(feedbackService.processFeedbackByPriority("88", payload, 100L)).thenReturn(serviceResult);

        Object body = controller.processByPriority("88", payload, request).getBody();
        assertThat(body).isEqualTo(Map.of("data", serviceResult));

        verify(adminAccessGuard).requireAdmin(request);
        verify(feedbackService).processFeedbackByPriority("88", payload, 100L);
    }

    @Test
    @DisplayName("getHighPriorityIssues_callsService")
    void getHighPriorityIssues_callsService() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Map<String, Object> serviceResult = Map.of("data", java.util.List.of(), "total", 0);
        when(feedbackService.getHighPriorityIssues(Boolean.TRUE, 1, 10)).thenReturn(serviceResult);

        Object body = controller.getHighPriorityIssues(Boolean.TRUE, 1, 10, request).getBody();
        assertThat(body).isEqualTo(serviceResult);

        verify(adminAccessGuard).requireAdmin(request);
        verify(feedbackService).getHighPriorityIssues(Boolean.TRUE, 1, 10);
    }
}
