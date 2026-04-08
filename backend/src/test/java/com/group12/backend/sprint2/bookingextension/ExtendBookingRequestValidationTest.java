package com.group12.backend.sprint2.bookingextension;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.group12.backend.dto.ExtendBookingRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * ID10/11 延长请求体验证（与 {@code *ExtendBooking*Test} 脚本过滤匹配）。
 */
@DisplayName("ID10/11 ExtendBookingRequest validation")
class ExtendBookingRequestValidationTest {

    private Validator validator;

    @BeforeEach
    void initValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("duration 为空时校验失败")
    void invalid_when_durationBlank() {
        ExtendBookingRequest req = new ExtendBookingRequest();
        Set<ConstraintViolation<ExtendBookingRequest>> violations = validator.validate(req);
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream().map(v -> v.getPropertyPath().toString()).collect(Collectors.toSet()))
                .contains("duration");
    }

    @Test
    @DisplayName("duration 非空时通过 Bean Validation")
    void valid_when_durationPresent() {
        ExtendBookingRequest req = new ExtendBookingRequest();
        req.setDuration("4H");
        assertThat(validator.validate(req)).isEmpty();
    }
}
