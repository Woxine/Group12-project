package com.group12.backend.service.impl;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    public Object getUserBookings(String userId, Integer page, Integer size) {
        Long uId = Long.parseLong(userId);
        int pageNum = (page != null && page > 0) ? page : 1;
        int pageSize = (size != null && size > 0) ? size : 10;

        Page<Booking> pageResult = bookingRepository.findByUser_IdOrderByStartTimeDesc(uId, PageRequest.of(pageNum - 1, pageSize));
        List<Booking> bookings = pageResult.getContent();

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        List<Map<String, Object>> data = new ArrayList<>();
        for (Booking booking : bookings) {
            data.add(bookingToMap(booking, fmt));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("data", data);
        result.put("total", pageResult.getTotalElements());
        return result;
    }

    @Override
    public Object getBookingById(String userId, String bookingId) {
        Long uId = Long.parseLong(userId);
        Long bId = Long.parseLong(bookingId);
        Booking booking = bookingRepository.findById(bId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        if (!booking.getUser().getId().equals(uId)) {
            throw new RuntimeException("Booking not found");
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        Map<String, Object> result = new HashMap<>();
        result.put("data", bookingToMap(booking, fmt));
        return result;
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

    private Map<String, Object> bookingToMap(Booking booking, DateTimeFormatter fmt) {
        String startStr = booking.getStartTime() == null ? "" : booking.getStartTime().format(fmt);
        String endStr = booking.getEndTime() == null ? "" : booking.getEndTime().format(fmt);
        String durationStr = formatDuration(booking.getDurationHours());
        Double price = booking.getTotalPrice() == null ? 0.0 : booking.getTotalPrice().doubleValue();

        Map<String, Object> m = new HashMap<>();
        m.put("id", String.valueOf(booking.getId()));
        m.put("scooterId", String.valueOf(booking.getScooter().getId()));
        m.put("userId", String.valueOf(booking.getUser().getId()));
        m.put("status", booking.getStatus());
        m.put("startTime", startStr);
        m.put("endTime", endStr);
        m.put("duration", durationStr);
        m.put("total_price", price);
        m.put("price", price);
        m.put("createdAt", startStr);
        m.put("start_lat", booking.getStartLat());
        m.put("start_lng", booking.getStartLng());
        m.put("end_lat", booking.getEndLat());
        m.put("end_lng", booking.getEndLng());
        return m;
    }

    private static String formatDuration(Double hours) {
        if (hours == null) return "1H";
        if (hours > 0 && hours <= 10.0 / 60.0 + 0.01) return "10M";
        if (hours <= 1) return "1H";
        if (hours <= 4) return "4H";
        if (hours <= 24) return "1D";
        if (hours <= 168) return "1W";
        return hours.intValue() + "H";
    }
}
