package com.group12.backend.sprint3state2.feedback;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.group12.backend.dto.ProcessFeedbackRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@DisplayName("ID14 ProcessFeedbackRequest")
class ProcessFeedbackRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

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

    @Test
    @DisplayName("invalid_when_actionNotInAllowedValues")
    void invalid_when_actionNotInAllowedValues() {
        ProcessFeedbackRequest request = new ProcessFeedbackRequest();
        request.setAction("RESOLVE");

        Set<String> paths = validator.validate(request)
                .stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertThat(paths).contains("action");
    }

    @Test
    @DisplayName("invalid_when_escalateWithoutTarget")
    void invalid_when_escalateWithoutTarget() {
        ProcessFeedbackRequest request = new ProcessFeedbackRequest();
        request.setAction("ESCALATE");
        request.setEscalateTo("  ");

        Set<ConstraintViolation<ProcessFeedbackRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream().map(v -> v.getPropertyPath().toString()).collect(Collectors.toSet()))
                .contains("escalateToValidForAction");
    }

    @Test
    @DisplayName("valid_when_directHandleWithoutTarget")
    void valid_when_directHandleWithoutTarget() {
        ProcessFeedbackRequest request = new ProcessFeedbackRequest();
        request.setAction("DIRECT_HANDLE");

        assertThat(validator.validate(request)).isEmpty();
    }
}
