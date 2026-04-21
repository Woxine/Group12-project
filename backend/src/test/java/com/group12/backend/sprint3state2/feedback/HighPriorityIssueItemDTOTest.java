package com.group12.backend.sprint3state2.feedback;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.group12.backend.dto.HighPriorityIssueItemDTO;

@DisplayName("ID15 HighPriorityIssueItemDTO")
class HighPriorityIssueItemDTOTest {

    @Test
    @DisplayName("getterSetter_roundTrip")
    void getterSetter_roundTrip() {
        HighPriorityIssueItemDTO dto = new HighPriorityIssueItemDTO();
        dto.setFeedbackId(21L);
        dto.setUserId(9L);
        dto.setScooterId(6L);
        dto.setContent("Brake issue");
        dto.setPriority("HIGH");
        dto.setEscalated(Boolean.TRUE);
        dto.setEscalatedTo("TECH_TEAM");
        dto.setResolved(Boolean.FALSE);

        assertThat(dto.getFeedbackId()).isEqualTo(21L);
        assertThat(dto.getUserId()).isEqualTo(9L);
        assertThat(dto.getScooterId()).isEqualTo(6L);
        assertThat(dto.getContent()).isEqualTo("Brake issue");
        assertThat(dto.getPriority()).isEqualTo("HIGH");
        assertThat(dto.getEscalated()).isTrue();
        assertThat(dto.getEscalatedTo()).isEqualTo("TECH_TEAM");
        assertThat(dto.getResolved()).isFalse();
    }
}
