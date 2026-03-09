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
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword())); 
        user.setRole("CUSTOMER");
        
        User savedUser = userRepository.save(user);

        return new RegisterResponse(
            String.valueOf(savedUser.getId()), 
            savedUser.getEmail(), 
            savedUser.getName()
        );
    }

    @Override
    public List<Object> getUserBookings(String userId) {
        Long uId = Long.parseLong(userId);
        List<Booking> bookings = bookingRepository.findByUserId(uId);

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
        
        return java.util.Map.of(
            "id", user.getId(),
            "email", user.getEmail(),
            "name", user.getName(),
            "role", user.getRole(),
            "isStudent", user.getIsStudent() != null ? user.getIsStudent() : false
        );
    }
}
