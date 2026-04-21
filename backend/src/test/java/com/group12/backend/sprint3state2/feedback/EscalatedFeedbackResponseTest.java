package com.group12.backend.sprint3state2.feedback;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.group12.backend.dto.EscalatedFeedbackResponse;

@DisplayName("ID14 EscalatedFeedbackResponse")
class EscalatedFeedbackResponseTest {

    @Test
    @DisplayName("getterSetter_roundTrip")
    void getterSetter_roundTrip() {
        EscalatedFeedbackResponse response = new EscalatedFeedbackResponse();
        response.setFeedbackId(12L);
        response.setPriority("HIGH");
        response.setEscalated(Boolean.TRUE);
        response.setEscalatedTo("MANAGEMENT");
        response.setStatus("ESCALATED");

        assertThat(response.getFeedbackId()).isEqualTo(12L);
        assertThat(response.getPriority()).isEqualTo("HIGH");
        assertThat(response.getEscalated()).isTrue();
        assertThat(response.getEscalatedTo()).isEqualTo("MANAGEMENT");
        assertThat(response.getStatus()).isEqualTo("ESCALATED");
    }
}
