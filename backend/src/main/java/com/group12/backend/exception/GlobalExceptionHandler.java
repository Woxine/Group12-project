package com.group12.backend.exception;

import java.util.Map;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for the application.
 * This class captures exceptions thrown by any Controller and transforms them into
 * standard JSON responses. It also implements security best practices by hiding
 * internal server error details (stack traces) from the client while exposing
 * safe business error messages.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理未知系统异常 (Exception)
     * 捕获所有未被专门处理的异常，防止堆栈信息泄露
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception e) {
        // Log the full stack trace internally for debugging
        // Logger.error("Unexpected error", e); 
        e.printStackTrace(); 
        // Minimal logging for now

        // Return a generic error message to the user
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", 500,
                    "error", "Internal Server Error",
                    "message", "An unexpected error occurred. Please contact support."
                ));
    }

    /**
     * 处理业务逻辑异常 (RuntimeException)
     * 例如：用户不存在、密码错误、邮箱已被注册等
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException e) {
        // 返回 400 Bad Request 状态码，方便前端直接通过 catch 捕获
        // SECURITY: Ensure e.getMessage() does not contain sensitive info (SQL, paths, etc.)
        // Developers must ensure thrown RuntimeExceptions strictly contain safe messages.
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", 400,
                    "error", "Business Error",
                    "message", e.getMessage()
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
                .body(Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", 400,
                    "error", "Validation Error",
                    "message", message
                ));
    }

    /**
     * 处理系统未知异常 (Exception)
     * 例如：数据库连接失败、空指针等
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception e) {
        e.printStackTrace(); // 打印堆栈以便后台调试
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", 500,
                    "error", "Internal Server Error",
                    "message", e.getClass().getName() + ": " + e.getMessage() // Show actual error for debugging
                ));
    }
}
