package com.group12.backend.sprint3state2.admin;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.group12.backend.exception.GlobalExceptionHandler;

@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("handleHttpMessageNotReadableException_returnsFriendlyMessage")
    void handleHttpMessageNotReadableException_returnsFriendlyMessage() {
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException(
                "Cannot deserialize value of type `java.lang.Double`");

        ResponseEntity<Object> response = handler.handleHttpMessageNotReadableException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body).isNotNull();
        String message = String.valueOf(body.get("message"));
        assertThat(message).isEqualTo("Invalid request format. Please provide valid numeric values.");
        assertThat(message.toLowerCase()).doesNotContain("java.lang");
    }

    @Test
    @DisplayName("handleRuntimeException_hidesTechnicalDetails")
    void handleRuntimeException_hidesTechnicalDetails() {
        RuntimeException exception = new RuntimeException("com.fasterxml.jackson.databind.exc.MismatchedInputException");

        ResponseEntity<Object> response = handler.handleRuntimeException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body).isNotNull();
        String message = String.valueOf(body.get("message"));
        assertThat(message).isEqualTo("The request could not be processed. Please check your input and try again.");
        assertThat(message.toLowerCase()).doesNotContain("com.fasterxml");
    }

    @Test
    @DisplayName("handleMethodArgumentTypeMismatchException_returnsParameterHint")
    void handleMethodArgumentTypeMismatchException_returnsParameterHint() {
        MethodArgumentTypeMismatchException exception = new MethodArgumentTypeMismatchException(
                "oops",
                Integer.class,
                "limit",
                null,
                null);

        ResponseEntity<Object> response = handler.handleMethodArgumentTypeMismatchException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body).isNotNull();
        assertThat(String.valueOf(body.get("message"))).isEqualTo("Invalid value for 'limit'.");
    }
}
