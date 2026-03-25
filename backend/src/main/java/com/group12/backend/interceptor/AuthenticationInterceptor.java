package com.group12.backend.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.group12.backend.exception.ErrorMessages;
import com.group12.backend.exception.ErrorResponseFactory;
import com.group12.backend.utils.JwtUtil;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        // 如果是 OPTIONS 请求（预检请求），直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); 

            try {
                if (jwtUtil.validateToken(token)) {
                    // 验证通过，提取用户信息放入 request 中，供 Controller 使用
                    Long userId = jwtUtil.extractUserId(token);
                    String email = jwtUtil.extractEmail(token);
                    
                    request.setAttribute("userId", userId);
                    request.setAttribute("email", email);
                    
                    return true;
                }
            } catch (Exception e) {
                // Token 解析失败或过期
            }
        }

        // 验证失败后处理，返回 401 Unauthorized
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(
                ErrorResponseFactory.build(
                        HttpStatus.UNAUTHORIZED,
                        ErrorMessages.UNAUTHORIZED,
                        ErrorMessages.ACCESS_DENIED_MESSAGE
                )));
        return false;
    }
}
