package com.group12.backend.service.impl;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.group12.backend.dto.RegisterRequest;
import com.group12.backend.dto.RegisterResponse;
import com.group12.backend.entity.Booking;
import com.group12.backend.entity.User;
import com.group12.backend.exception.BusinessException;
import com.group12.backend.exception.ErrorMessages;
import com.group12.backend.repository.BookingRepository;
import com.group12.backend.repository.UserRepository;
import com.group12.backend.service.UserService;

/**
 * 实现用户注册、资料查询和预约历史查询相关的业务逻辑。
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    /**
     * 校验注册信息并创建新的客户账户。
     */
    public Object register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException(ErrorMessages.EMAIL_ALREADY_REGISTERED, HttpStatus.CONFLICT);
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
    /**
     * 分页查询指定用户的预约历史，并组装前端需要的返回结构。
     */
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
    /**
     * 查询指定用户名下的单条预约详情。
     */
    public Object getBookingById(String userId, String bookingId) {
        Long uId = Long.parseLong(userId);
        Long bId = Long.parseLong(bookingId);
        Booking booking = bookingRepository.findById(bId)
                .orElseThrow(() -> new BusinessException(ErrorMessages.BOOKING_NOT_FOUND, HttpStatus.NOT_FOUND));
        if (!booking.getUser().getId().equals(uId)) {
            throw new BusinessException(ErrorMessages.BOOKING_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        Map<String, Object> result = new HashMap<>();
        result.put("data", bookingToMap(booking, fmt));
        return result;
    }

    @Override
    /**
     * 获取指定用户的个人资料信息。
     */
    public Object getUserProfile(String userId) {
        Long uId = Long.parseLong(userId);
        User user = userRepository.findById(uId)
                .orElseThrow(() -> new BusinessException(ErrorMessages.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        return java.util.Map.of(
            "id", user.getId(),
            "email", user.getEmail(),
            "name", user.getName(),
            "role", user.getRole(),
            "isStudent", user.getIsStudent() != null ? user.getIsStudent() : false
        );
    }

    /**
     * 将预约实体转换为前端展示所需的字段结构。
     */
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

    /**
     * 根据预约时长小时数映射为前端使用的租期标签。
     */
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
