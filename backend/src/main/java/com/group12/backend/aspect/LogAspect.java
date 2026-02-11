package com.group12.backend.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group12.backend.annotation.LogAction;
import com.group12.backend.entity.AuditLog;
import com.group12.backend.repository.AuditLogRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
public class LogAspect {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Around("@annotation(com.group12.backend.annotation.LogAction)")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // 1. Execute the method
        Object result = joinPoint.proceed();

        try {
            // 2. Capture details after successful execution
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            LogAction logAction = method.getAnnotation(LogAction.class);

            Object[] args = joinPoint.getArgs();
            String paramsJson = "";
            Long extractedUserId = null;

            if (args.length > 0) {
                Object arg0 = args[0];
                // Try to serialize the first argument (usually the DTO)
                try {
                    paramsJson = objectMapper.writeValueAsString(arg0);
                } catch (Exception e) {
                    paramsJson = "Could not serialize args: " + e.getMessage();
                }

                // Try to extract userId from the DTO via reflection
                try {
                    // Try getUserId() or getUser_id()
                    Method getUserIdMethod = null;
                    try {
                        getUserIdMethod = arg0.getClass().getMethod("getUser_id");
                    } catch (NoSuchMethodException e) {
                        try {
                            getUserIdMethod = arg0.getClass().getMethod("getUserId");
                        } catch (NoSuchMethodException ignored) {}
                    }

                    if (getUserIdMethod != null) {
                        Object remoteId = getUserIdMethod.invoke(arg0);
                        if (remoteId instanceof String) {
                            extractedUserId = Long.parseLong((String) remoteId);
                        } else if (remoteId instanceof Long) {
                            extractedUserId = (Long) remoteId;
                        }
                    }
                } catch (Exception ignored) {
                    // Start of extraction failure, just ignore
                }
            }
            
            AuditLog log = new AuditLog();
            log.setAction(logAction.action());
            log.setEntityName(logAction.entityName());
            log.setRequestParams(paramsJson);
            log.setTimestamp(LocalDateTime.now());
            if (extractedUserId != null) {
                log.setUserId(extractedUserId);
            }

            
            // Try to set result ID if available (e.g. BookingResponse)
            if (result != null) {
                 // Simple attempt to extract ID if the result object has a getId() method
                 try {
                     Method getId = result.getClass().getMethod("getId");
                     Object idVal = getId.invoke(result);
                     log.setEntityId(String.valueOf(idVal));
                 } catch (Exception ignored) {
                     // If no getId method, maybe it's in the response fields
                 }
            }

            auditLogRepository.save(log);

        } catch (Exception e) {
            // Log logging failure, but don't fail the request
            System.err.println("Failed to save audit log: " + e.getMessage());
        }

        return result;
    }
}
