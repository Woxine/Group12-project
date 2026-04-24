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
import com.group12.backend.dto.ChangePasswordRequest;
import com.group12.backend.dto.ChangeEmailRequest;
import com.group12.backend.dto.ChangeNameRequest;
import com.group12.backend.dto.RegisterResponse;
import com.group12.backend.dto.UpdateProfileRequest;
import com.group12.backend.entity.Booking;
import com.group12.backend.entity.User;
import com.group12.backend.exception.BusinessException;
import com.group12.backend.exception.ErrorMessages;
import com.group12.backend.repository.BookingRepository;
import com.group12.backend.repository.UserRepository;
import com.group12.backend.service.BillingRule;
import com.group12.backend.service.BillingService;
import com.group12.backend.service.DiscountService;
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

    @Autowired
    private BillingService billingService;

    @Autowired(required = false)
    private DiscountService discountService;

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
    public Object getGuestBookings(String guestId, Integer page, Integer size) {
        Long guestUserId = resolveGuestUserId(guestId);
        return getUserBookings(String.valueOf(guestUserId), page, size);
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

        String discountType = "NONE";
        boolean hasFrequentDiscount = false;
        boolean hasStudentDiscount = false;
        boolean hasSeniorDiscount = false;
        if (discountService != null) {
            discountType = discountService.resolveDiscountType(uId);
            hasFrequentDiscount = discountService.hasFrequentDiscount(uId);
            hasStudentDiscount = discountService.hasStudentDiscount(uId);
            hasSeniorDiscount = discountService.hasSeniorDiscount(uId);
        }

        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("email", user.getEmail());
        profile.put("name", user.getName());
        profile.put("role", user.getRole());
        profile.put("isStudent", user.getIsStudent() != null ? user.getIsStudent() : false);
        profile.put("age", user.getAge());
        profile.put("discountType", discountType);
        profile.put("hasFrequentDiscount", hasFrequentDiscount);
        profile.put("hasStudentDiscount", hasStudentDiscount);
        profile.put("hasSeniorDiscount", hasSeniorDiscount);
        return profile;
    }

    @Override
    public Object updateUserProfile(String userId, UpdateProfileRequest request) {
        Long uId = Long.parseLong(userId);
        User user = userRepository.findById(uId)
                .orElseThrow(() -> new BusinessException(ErrorMessages.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (request.getIsStudent() != null) {
            user.setIsStudent(request.getIsStudent());
        }
        if (request.getAge() != null) {
            user.setAge(request.getAge());
        }
        User saved = userRepository.save(user);
        return java.util.Map.of(
                "message", "Profile updated successfully",
                "isStudent", saved.getIsStudent() != null ? saved.getIsStudent() : false,
                "age", saved.getAge());
    }

    @Override
    /**
     * 验证原密码并更新为新密码（加密后落库）。
     */
    public Object changePassword(String userId, ChangePasswordRequest request) {
        Long uId = Long.parseLong(userId);
        User user = userRepository.findById(uId)
                .orElseThrow(() -> new BusinessException(ErrorMessages.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException(ErrorMessages.PASSWORD_INCORRECT, HttpStatus.BAD_REQUEST);
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BusinessException(ErrorMessages.SAME_PASSWORD_NOT_ALLOWED, HttpStatus.BAD_REQUEST);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return java.util.Map.of("message", "Password updated successfully");
    }

    @Override
    /**
     * 修改邮箱前校验：与当前邮箱不同，且数据库中未被其他账号占用。
     */
    public Object changeEmail(String userId, ChangeEmailRequest request) {
        Long uId = Long.parseLong(userId);
        User user = userRepository.findById(uId)
                .orElseThrow(() -> new BusinessException(ErrorMessages.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        String newEmail = request.getNewEmail() == null ? "" : request.getNewEmail().trim().toLowerCase();
        String currentEmail = user.getEmail() == null ? "" : user.getEmail().trim().toLowerCase();

        if (newEmail.equals(currentEmail)) {
            throw new BusinessException(ErrorMessages.SAME_EMAIL_NOT_ALLOWED, HttpStatus.BAD_REQUEST);
        }

        userRepository.findByEmailIgnoreCase(newEmail).ifPresent(existingUser -> {
            if (!existingUser.getId().equals(user.getId())) {
                throw new BusinessException(ErrorMessages.EMAIL_ALREADY_REGISTERED, HttpStatus.CONFLICT);
            }
        });

        user.setEmail(newEmail);
        userRepository.save(user);

        return java.util.Map.of(
                "message", "Email updated successfully",
                "email", user.getEmail());
    }

    @Override
    /**
     * 修改用户名前校验：与当前用户名不同，且数据库中未被其他账号占用。
     */
    public Object changeName(String userId, ChangeNameRequest request) {
        Long uId = Long.parseLong(userId);
        User user = userRepository.findById(uId)
                .orElseThrow(() -> new BusinessException(ErrorMessages.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        String newName = request.getNewName() == null ? "" : request.getNewName().trim();
        String currentName = user.getName() == null ? "" : user.getName().trim();

        if (newName.equalsIgnoreCase(currentName)) {
            throw new BusinessException(ErrorMessages.SAME_NAME_NOT_ALLOWED, HttpStatus.BAD_REQUEST);
        }

        userRepository.findByNameIgnoreCase(newName).ifPresent(existingUser -> {
            if (!existingUser.getId().equals(user.getId())) {
                throw new BusinessException(ErrorMessages.USERNAME_ALREADY_TAKEN, HttpStatus.CONFLICT);
            }
        });

        user.setName(newName);
        userRepository.save(user);

        return java.util.Map.of(
                "message", "Username updated successfully",
                "name", user.getName());
    }

    /**
     * 将预约实体转换为前端展示所需的字段结构。
     */
    private Map<String, Object> bookingToMap(Booking booking, DateTimeFormatter fmt) {
        String startStr = booking.getStartTime() == null ? "" : booking.getStartTime().format(fmt);
        String endStr = booking.getEndTime() == null ? "" : booking.getEndTime().format(fmt);
        String paymentDeadlineStr = booking.getPaymentDeadline() == null ? null : booking.getPaymentDeadline().format(fmt);
        String durationStr = formatDuration(booking.getDurationHours());
        Double price = booking.getTotalPrice() == null ? 0.0 : booking.getTotalPrice().doubleValue();
        Double originalPrice = booking.getOriginalPrice() == null ? null : booking.getOriginalPrice().doubleValue();
        Double discountAmount = booking.getDiscountAmount() == null ? null : booking.getDiscountAmount().doubleValue();

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
        m.put("original_price", originalPrice);
        m.put("discount_amount", discountAmount);
        m.put("discount_multiplier", booking.getDiscountMultiplier());
        m.put("discount_type", booking.getDiscountType());
        m.put("paymentDeadline", paymentDeadlineStr);
        m.put("payment_deadline", paymentDeadlineStr);
        Double hourRate = booking.getScooter().getHourRate() == null ? null : booking.getScooter().getHourRate().doubleValue();
        BillingRule billingRule = billingService.getCurrentRule();
        m.put("hourRate", hourRate);
        m.put("hour_rate", hourRate);
        m.put("longRentThresholdHours", billingRule.longRentThresholdHours() == null ? null : billingRule.longRentThresholdHours().doubleValue());
        m.put("extraLongRentThresholdHours", billingRule.extraLongRentThresholdHours() == null ? null : billingRule.extraLongRentThresholdHours().doubleValue());
        m.put("longRentHourRateMultiplier", billingRule.longRentHourRateMultiplier() == null ? null : billingRule.longRentHourRateMultiplier().doubleValue());
        m.put("extraLongRentHourRateMultiplier", billingRule.extraLongRentHourRateMultiplier() == null ? null : billingRule.extraLongRentHourRateMultiplier().doubleValue());
        m.put("createdAt", startStr);
        m.put("start_lat", booking.getStartLat());
        m.put("start_lng", booking.getStartLng());
        m.put("end_lat", booking.getEndLat());
        m.put("end_lng", booking.getEndLng());
        return m;
    }

    /**
     * 根据预约时长小时数映射为前端展示文案。
     * 先匹配单次预订档位（10M/1H/…）；延长后总时长多为非档位组合，改为按分钟或「Xh Ym」展示，避免把 20 分钟误标成 1H。
     */
    private static String formatDuration(Double hours) {
        if (hours == null || hours <= 0) {
            return "-";
        }
        final double eps = 0.01;
        double h = hours;
        if (Math.abs(h - 10.0 / 60.0) < eps) {
            return "10M";
        }
        if (Math.abs(h - 1.0) < eps) {
            return "1H";
        }
        if (Math.abs(h - 4.0) < eps) {
            return "4H";
        }
        if (Math.abs(h - 24.0) < eps) {
            return "1D";
        }
        if (Math.abs(h - 168.0) < eps) {
            return "1W";
        }
        long totalMinutes = Math.round(h * 60.0);
        if (totalMinutes < 60) {
            return totalMinutes + " min";
        }
        long wholeHours = totalMinutes / 60;
        long remainderMin = totalMinutes % 60;
        if (remainderMin == 0) {
            return wholeHours + "h";
        }
        return wholeHours + "h " + remainderMin + "m";
    }

    private Long resolveGuestUserId(String guestId) {
        if (guestId == null) {
            throw new BusinessException("guestId is invalid", HttpStatus.BAD_REQUEST);
        }
        String digits = guestId.replaceAll("\\D+", "");
        if (digits.isEmpty()) {
            throw new BusinessException("guestId is invalid", HttpStatus.BAD_REQUEST);
        }
        Long parsed = Long.parseLong(digits);
        if (userRepository.findById(parsed).isPresent()) {
            return parsed;
        }
        String placeholderEmail = "guest+" + parsed + "@placeholder.local";
        return userRepository.findByEmailIgnoreCase(placeholderEmail)
                .map(User::getId)
                .orElseThrow(() -> new BusinessException(ErrorMessages.USER_NOT_FOUND, HttpStatus.NOT_FOUND));
    }
}
