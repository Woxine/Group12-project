package com.group12.backend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.group12.backend.entity.User;
import com.group12.backend.exception.BusinessException;
import com.group12.backend.exception.ErrorMessages;
import com.group12.backend.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Validates whether current authenticated user has ADMIN role.
 */
@Component
public class AdminAccessGuard {

    @Autowired
    private UserRepository userRepository;

    public void requireAdmin(HttpServletRequest request) {
        Object authUserId = request.getAttribute("userId");
        if (authUserId == null) {
            throw new BusinessException(ErrorMessages.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }

        Long userId = Long.valueOf(String.valueOf(authUserId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorMessages.USER_NOT_FOUND, HttpStatus.UNAUTHORIZED));

        if (user.getRole() == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            throw new BusinessException(ErrorMessages.FORBIDDEN, HttpStatus.FORBIDDEN);
        }
    }
}
