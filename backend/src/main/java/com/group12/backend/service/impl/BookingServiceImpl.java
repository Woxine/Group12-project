package com.group12.backend.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.group12.backend.dto.BookingResponse;
import com.group12.backend.dto.CreateBookingRequest;
import com.group12.backend.entity.Booking;
import com.group12.backend.entity.Scooter;
import com.group12.backend.entity.User;
import com.group12.backend.repository.BookingRepository;
import com.group12.backend.repository.ScooterRepository;
import com.group12.backend.repository.UserRepository;
import com.group12.backend.service.BookingService;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ScooterRepository scooterRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional // 开启事务，保证扣款、修改车辆状态、生成订单原子性
    public Object createBooking(CreateBookingRequest request) {
        Long userId = Long.parseLong(request.getUser_id());
        Long scooterId = Long.parseLong(request.getScooter_id());
        String durationRequest = request.getDuration(); // 1H, 4H, 1D, 1W

        // 1. 校验用户
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. 校验该用户是否已有进行中的预订（不允许重复预订）
        java.util.List<Booking> activeBookings = bookingRepository.findByUser_IdAndStatus(userId, "CONFIRMED");
        if (activeBookings != null && !activeBookings.isEmpty()) {
            throw new RuntimeException("You already have an active booking. Please cancel it or wait until it ends before booking again.");
        }

        // 3. 校验车辆
        Scooter scooter = scooterRepository.findById(scooterId)
                .orElseThrow(() -> new RuntimeException("Scooter not found"));

        // 4. 校验车辆状态
        if (!"AVAILABLE".equalsIgnoreCase(scooter.getStatus())) {
            throw new RuntimeException("Scooter is not available (Current status: " + scooter.getStatus() + ")");
        }

        // 5. 计算固定期限的结束时间和价格
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime;
        Double durationHours;
        
        if ("1H".equalsIgnoreCase(durationRequest)) {
            durationHours = 1.0;
        } else if ("4H".equalsIgnoreCase(durationRequest)) {
            durationHours = 4.0;
        } else if ("1D".equalsIgnoreCase(durationRequest)) {
            durationHours = 24.0;
        } else if ("1W".equalsIgnoreCase(durationRequest)) {
            durationHours = 168.0;
        } else {
            // 默认 1 小时 或者 抛出异常
            durationHours = 1.0; 
        }
        
        endTime = startTime.plusHours(durationHours.longValue());
        
        // 计算总价 (这里假设 hourRate 是每小时单价)
        java.math.BigDecimal rate = scooter.getHourRate();
        java.math.BigDecimal totalPrice = rate.multiply(java.math.BigDecimal.valueOf(durationHours));

        // 6. 创建订单
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setScooter(scooter);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setDurationHours(durationHours);
        booking.setTotalPrice(totalPrice);
        booking.setStatus("CONFIRMED"); // 初始状态为已确认
        
        Booking savedBooking = bookingRepository.save(booking);

        // 7. 更新车辆状态为占用
        scooter.setStatus("RENTED");
        scooterRepository.save(scooter);

        // 8. 返回结果（createdAt 用字符串避免 Jackson 序列化 LocalDateTime 报错）
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String createdAtStr = savedBooking.getStartTime() == null ? "" : savedBooking.getStartTime().format(fmt);
        return new BookingResponse(
            String.valueOf(savedBooking.getId()),
            String.valueOf(scooter.getId()),
            String.valueOf(user.getId()),
            savedBooking.getStatus(),
            createdAtStr
        );
    }

    @Override
    @Transactional
    public Object cancelBooking(String bookingId) {
        Long id = Long.parseLong(bookingId);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // 只有进行中的订单可以取消（这里简化逻辑，假设 CONFIRMED 即为开始行程但未结束）
        // 实际业务中可能区分 "RESERVED" (预约中) 和 "IN_PROGRESS" (骑行中)
        if (!"CONFIRMED".equals(booking.getStatus())) {
            throw new RuntimeException("Cannot cancel booking with status: " + booking.getStatus());
        }

        // 更新订单状态
        booking.setStatus("CANCELLED");
        booking.setEndTime(LocalDateTime.now());
        bookingRepository.save(booking);

        // 释放车辆
        Scooter scooter = booking.getScooter();
        scooter.setStatus("AVAILABLE");
        scooterRepository.save(scooter);

        return "Booking cancelled successfully";
    }
}
