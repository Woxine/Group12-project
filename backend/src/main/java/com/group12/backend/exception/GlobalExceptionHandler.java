package com.group12.backend.exception;

import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * Global exception handler for the application.
 * This class captures exceptions thrown by any Controller and transforms them into
 * standard JSON responses. It also implements security best practices by hiding
 * internal server error details (stack traces) from the client while exposing
 * safe business error messages.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private HttpHeaders corsHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Origin", "*");
        headers.set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        headers.set("Access-Control-Allow-Headers", "*");
        headers.set("Access-Control-Max-Age", "3600");
        return headers;
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Object> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        System.err.println("Method not supported: " + e.getMethod() + " " + e.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .headers(corsHeaders())
                .body(ErrorResponseFactory.build(
                        HttpStatus.METHOD_NOT_ALLOWED,
                        "METHOD_NOT_ALLOWED",
                        "Request method '" + e.getMethod() + "' is not supported."
                ));
    }

    /**
     * 处理未知系统异常 (Exception)
     * 捕获所有未被专门处理的异常，防止堆栈信息泄露
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception e) {
        System.err.println("Unexpected error: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .headers(corsHeaders())
                .body(ErrorResponseFactory.build(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        ErrorMessages.INTERNAL_SERVER_ERROR,
                        ErrorMessages.UNEXPECTED_ERROR_MESSAGE
                ));
    }

    /**
     * 处理业务逻辑异常 (RuntimeException)
     * 例如：用户不存在、密码错误、邮箱已被注册等
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleBusinessException(BusinessException e) {
        HttpStatus status = e.getStatus() != null ? e.getStatus() : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status)
                .headers(corsHeaders())
                .body(ErrorResponseFactory.build(
                        status,
                        ErrorMessages.BUSINESS_ERROR,
                        e.getMessage()
                ));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .headers(corsHeaders())
                .body(ErrorResponseFactory.build(
                        HttpStatus.CONFLICT,
                        ErrorMessages.BUSINESS_ERROR,
                        ErrorMessages.BOOKING_CONCURRENT_CONFLICT
                ));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Object> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .headers(corsHeaders())
                .body(ErrorResponseFactory.build(
                        HttpStatus.BAD_REQUEST,
                        ErrorMessages.VALIDATION_ERROR,
                        ErrorMessages.FILE_EXCEEDS_MAX_ALLOWED_SIZE
                ));
    }

    /**
     * 兼容仍未迁移的 RuntimeException
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .headers(corsHeaders())
                .body(ErrorResponseFactory.build(
                        HttpStatus.BAD_REQUEST,
                        ErrorMessages.BUSINESS_ERROR,
                        "The request could not be processed. Please check your input and try again."
                ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .headers(corsHeaders())
                .body(ErrorResponseFactory.build(
                        HttpStatus.BAD_REQUEST,
                        ErrorMessages.VALIDATION_ERROR,
                        "Invalid request format. Please provide valid numeric values."
                ));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String parameterName = e.getName() == null ? "parameter" : e.getName();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .headers(corsHeaders())
                .body(ErrorResponseFactory.build(
                        HttpStatus.BAD_REQUEST,
                        ErrorMessages.VALIDATION_ERROR,
                        "Invalid value for '" + parameterName + "'."
                ));
    }

    /**
     * 处理参数校验异常 (MethodArgumentNotValidException)
     * 当 @Valid 校验失败时触发
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .headers(corsHeaders())
                .body(ErrorResponseFactory.build(
                        HttpStatus.BAD_REQUEST,
                        ErrorMessages.VALIDATION_ERROR,
                        message
                ));
    }


}
