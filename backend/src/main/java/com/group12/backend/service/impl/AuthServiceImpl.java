package com.group12.backend.service.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.group12.backend.dto.LoginRequest;
import com.group12.backend.dto.LoginResponse;
import com.group12.backend.entity.User;
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
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. 验证密码 (使用 BCrypt 匹配)
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // 3. 生成真实 JWT Token
        String token = jwtUtil.generateToken(user.getEmail(), user.getId());

        // 4. 返回登录信息
        return new LoginResponse(
            token, // 不需要再加 "Bearer " 前缀，前端通常会自己处理，或者在这里加也行。根据代码习惯，这里只返回token字符串
            String.valueOf(user.getId()),
            user.getRole()
        );
    }

    @Override
    public Object checkPermission() {
        // 这是一个模拟接口，实际应从 SecurityContext 获取当前用户
        return "Permission Valid";
    }
}
