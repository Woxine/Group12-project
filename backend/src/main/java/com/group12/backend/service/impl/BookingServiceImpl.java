package com.group12.backend.service.impl;

import java.time.LocalDateTime;

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
    @Transactional
    public Object createBooking(CreateBookingRequest request) {
        Long userId = Long.parseLong(request.getUser_id());
        Long scooterId = Long.parseLong(request.getScooter_id());
        String durationRequest = request.getDuration(); // 1H, 4H, 1D, 1W

        // 1. 校验用户
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. 校验车辆
        Scooter scooter = scooterRepository.findById(scooterId)
                .orElseThrow(() -> new RuntimeException("Scooter not found"));

        // 3. 校验车辆状态
        if (!"AVAILABLE".equalsIgnoreCase(scooter.getStatus())) {
            throw new RuntimeException("Scooter is not available (Current status: " + scooter.getStatus() + ")");
        }

        // 4. 计算固定期限的结束时间和价格
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
            durationHours = 1.0; 
        }
        
        endTime = startTime.plusHours(durationHours.longValue());
        
        java.math.BigDecimal rate = scooter.getHourRate();
        java.math.BigDecimal totalPrice = rate.multiply(java.math.BigDecimal.valueOf(durationHours));

        // 5. 创建订单
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setScooter(scooter);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setDurationHours(durationHours);
        booking.setTotalPrice(totalPrice);
        booking.setStatus("CONFIRMED");
        
        Booking savedBooking = bookingRepository.save(booking);

        // 6. 更新车辆状态为占用
        scooter.setStatus("RENTED");
        scooterRepository.save(scooter);

        // 7. 返回结果
        return new BookingResponse(
            String.valueOf(savedBooking.getId()),
            String.valueOf(scooter.getId()),
            String.valueOf(user.getId()),
            savedBooking.getStatus(),
            savedBooking.getStartTime()
        );
    }

    @Override
    @Transactional
    public Object cancelBooking(String bookingId) {
        Long id = Long.parseLong(bookingId);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!"CONFIRMED".equals(booking.getStatus())) {
            throw new RuntimeException("Cannot cancel booking with status: " + booking.getStatus());
        }

        booking.setStatus("CANCELLED");
        booking.setEndTime(LocalDateTime.now());
        bookingRepository.save(booking);

        Scooter scooter = booking.getScooter();
        scooter.setStatus("AVAILABLE");
        scooterRepository.save(scooter);

        return "Booking cancelled successfully";
    }
}
