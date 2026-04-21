package com.group12.backend.sprint3state2.feedback;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.group12.backend.dto.ProcessFeedbackRequest;

@DisplayName("ID14 ProcessFeedbackRequest")
class ProcessFeedbackRequestTest {

    @Test
    @DisplayName("getterSetter_roundTrip")
    void getterSetter_roundTrip() {
        ProcessFeedbackRequest request = new ProcessFeedbackRequest();
        request.setAction("ESCALATE");
        request.setEscalateTo("OPS_TEAM");
        request.setNote("Need escalation");

        assertThat(request.getAction()).isEqualTo("ESCALATE");
        assertThat(request.getEscalateTo()).isEqualTo("OPS_TEAM");
        assertThat(request.getNote()).isEqualTo("Need escalation");
    }
}
