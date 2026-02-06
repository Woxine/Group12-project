package com.group12.backend.service.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.group12.backend.dto.LoginRequest;
import com.group12.backend.dto.LoginResponse;
import com.group12.backend.entity.User;
import com.group12.backend.repository.UserRepository;
import com.group12.backend.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Object login(LoginRequest request) {
        // 1. 查找用户
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. 验证密码 (简单比对)
        // TODO: 生产环境应使用 BCryptPasswordEncoder.matches()
        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // 3. 生成 Token (模拟 JWT)
        String token = "Bearer " + UUID.randomUUID().toString();

        // 4. 返回登录信息
        return new LoginResponse(
            token,
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
