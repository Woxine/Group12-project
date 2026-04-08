package com.group12.backend.sprint2.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.group12.backend.dto.LoginRequest;
import com.group12.backend.dto.LoginResponse;
import com.group12.backend.entity.User;
import com.group12.backend.exception.BusinessException;
import com.group12.backend.exception.ErrorMessages;
import com.group12.backend.repository.UserRepository;
import com.group12.backend.service.impl.AuthServiceImpl;
import com.group12.backend.utils.JwtUtil;

/**
 * ID3 认证：{@link AuthServiceImpl#login} 已实现，对其成功路径与统一错误消息做单元测试。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ID3 AuthService login")
class AuthServiceLoginTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    @DisplayName("邮箱与密码正确时返回 LoginResponse 并签发 token")
    void login_succeeds_whenCredentialsValid() {
        User user = new User();
        user.setId(10L);
        user.setEmail("user@example.com");
        user.setPassword("ENC_HASH");
        user.setName("Test User");
        user.setRole("CUSTOMER");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret", "ENC_HASH")).thenReturn(true);
        when(jwtUtil.generateToken("user@example.com", 10L)).thenReturn("jwt-token");

        LoginRequest req = new LoginRequest();
        req.setEmail("user@example.com");
        req.setPassword("secret");

        Object result = authService.login(req);

        assertThat(result).isInstanceOf(LoginResponse.class);
        LoginResponse lr = (LoginResponse) result;
        assertThat(lr.getToken()).isEqualTo("jwt-token");
        assertThat(lr.getUserId()).isEqualTo("10");
        assertThat(lr.getRole()).isEqualTo("CUSTOMER");
        assertThat(lr.getName()).isEqualTo("Test User");
        verify(jwtUtil).generateToken("user@example.com", 10L);
    }

    @Test
    @DisplayName("用户不存在时抛出 BusinessException 且不泄露用户枚举信息")
    void login_failsWithGenericMessage_whenUserMissing() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        LoginRequest req = new LoginRequest();
        req.setEmail("missing@example.com");
        req.setPassword("any");

        BusinessException ex = assertThrows(BusinessException.class, () -> authService.login(req));
        assertThat(ex.getMessage()).isEqualTo(ErrorMessages.INVALID_EMAIL_OR_PASSWORD);
        verify(jwtUtil, never()).generateToken(anyString(), anyLong());
    }

    @Test
    @DisplayName("密码错误时抛出 BusinessException")
    void login_fails_whenPasswordMismatch() {
        User user = new User();
        user.setId(1L);
        user.setEmail("u@example.com");
        user.setPassword("HASH");
        when(userRepository.findByEmail("u@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "HASH")).thenReturn(false);

        LoginRequest req = new LoginRequest();
        req.setEmail("u@example.com");
        req.setPassword("wrong");

        BusinessException ex = assertThrows(BusinessException.class, () -> authService.login(req));
        assertThat(ex.getMessage()).isEqualTo(ErrorMessages.INVALID_EMAIL_OR_PASSWORD);
        verify(jwtUtil, never()).generateToken(anyString(), anyLong());
    }

    @Test
    @DisplayName("用户不存在时会额外调用一次 bcrypt 以缓解时序侧信道（与实现一致）")
    void login_whenUserMissing_performsDummyPasswordCheck() {
        when(userRepository.findByEmail("x@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.matches(eq("p"), eq("$2a$10$............................................................")))
                .thenReturn(false);

        LoginRequest req = new LoginRequest();
        req.setEmail("x@example.com");
        req.setPassword("p");

        assertThrows(BusinessException.class, () -> authService.login(req));

        verify(passwordEncoder, times(1)).matches(eq("p"), eq("$2a$10$............................................................"));
    }

    @Test
    @DisplayName("checkPermission 返回占位结果")
    void checkPermission_returnsPlaceholder() {
        assertThat(authService.checkPermission()).isEqualTo("Permission Valid");
    }
}
