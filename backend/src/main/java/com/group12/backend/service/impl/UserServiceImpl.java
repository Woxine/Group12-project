package com.group12.backend.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.group12.backend.dto.BookingResponse;
import com.group12.backend.dto.RegisterRequest;
import com.group12.backend.dto.RegisterResponse;
import com.group12.backend.entity.Booking;
import com.group12.backend.entity.User;
import com.group12.backend.repository.BookingRepository;
import com.group12.backend.repository.UserRepository;
import com.group12.backend.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Object register(RegisterRequest request) {
        // 1. 检查邮箱是否已存在
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        // 2. 创建用户实体
        User user = new User();
        user.setEmail(request.getEmail());
        // 使用 BCrypt 加密密码
        user.setPassword(passwordEncoder.encode(request.getPassword())); 
        user.setRole("CUSTOMER"); // 默认角色
        
        // 3. 保存入库
        User savedUser = userRepository.save(user);

        // 4. 返回响应 DTO
        return new RegisterResponse(
            String.valueOf(savedUser.getId()), 
            savedUser.getEmail(), 
            request.getName() // 注意：User目前可能没有 name 字段，直接返回请求中的 name
        );
    }

    @Override
    public List<Object> getUserBookings(String userId) {
        Long uId = Long.parseLong(userId);
        List<Booking> bookings = bookingRepository.findByUserId(uId);

        // 将 Entity 转换为 DTO
        return bookings.stream().map(booking -> new BookingResponse(
            String.valueOf(booking.getId()),
            String.valueOf(booking.getScooter().getId()),
            String.valueOf(booking.getUser().getId()),
            booking.getStatus(),
            booking.getStartTime()
        )).collect(Collectors.toList());
    }

    @Override
    public Object getUserProfile(String userId) {
        Long uId = Long.parseLong(userId);
        User user = userRepository.findById(uId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // 简单返回用户基本信息 map
        // 实际项目应封装 UserProfileDTO
        return java.util.Map.of(
            "id", user.getId(),
            "email", user.getEmail(),
            "role", user.getRole(),
            "isStudent", user.getIsStudent() != null ? user.getIsStudent() : false
        );
    }
}
