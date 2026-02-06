package com.group12.backend.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.group12.backend.utils.JwtUtil;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果是 OPTIONS 请求（预检请求），直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // 去掉 "Bearer " 前缀

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

        // 验证失败
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"status\": 401, \"error\": \"Unauthorized\", \"message\": \"Access denied. Invalid or missing token.\"}");
        return false;
    }
}
