package com.group12.backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.group12.backend.dto.LoginRequest;
import com.group12.backend.dto.LoginResponse;
import com.group12.backend.entity.User;
import com.group12.backend.exception.BusinessException;
import com.group12.backend.exception.ErrorMessages;
import com.group12.backend.repository.UserRepository;
import com.group12.backend.service.AuthService;
import com.group12.backend.utils.JwtUtil;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Object login(LoginRequest request) {
        // 1. 查找用户
        // SECURITY: Do not reveal whether user exists or not explicitly.
        // But for internal logic we need to find it.
        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        // 2. 验证密码 (使用 BCrypt 匹配)
        // SECURITY: Use 'Invalid email or password' for both cases to prevent user enumeration
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
             // Simulate computation time if user not found to prevent timing attacks? 
             // (Advanced, usually relying on framework, but logic should be generic)
             if (user == null) {
                 // Dummy check to consume time similar to BCrypt check
                 passwordEncoder.matches(request.getPassword(), "$2a$10$............................................................");
             }
             throw new BusinessException(ErrorMessages.INVALID_EMAIL_OR_PASSWORD);
        }

        // 3. 生成真实 JWT Token
        String token = jwtUtil.generateToken(user.getEmail(), user.getId());

        // 4. 返回登录信息
        return new LoginResponse(
            token,
            String.valueOf(user.getId()),
            user.getRole(),
            user.getName()
        );
    }

    @Override
    public Object checkPermission() {
        // 这是一个模拟接口，实际应从 SecurityContext 获取当前用户
        return "Permission Valid";
    }
}
