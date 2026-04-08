package com.group12.backend.sprint2.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import com.group12.backend.controller.SecurityController;
import com.group12.backend.dto.SecuritySettingsRequest;
import com.group12.backend.dto.SecuritySettingsResponse;
import com.group12.backend.service.SecurityService;
import com.group12.backend.utils.JwtUtil;

/**
 * ID3 安全设置 HTTP：断言控制器已委托 {@link SecurityService} 且返回 2xx；骨架阶段失败。
 */
@WebMvcTest(controllers = SecurityController.class)
@DisplayName("ID3 SecurityController (WebMvc)")
class SecurityControllerMvcTest {

    private static final String AUTH = "Bearer unit-test-token";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SecurityService securityService;

    @MockBean
    private JwtUtil jwtUtil;

    @BeforeEach
    void stub() {
        when(jwtUtil.validateToken(anyString())).thenReturn(true);
        when(jwtUtil.extractUserId(anyString())).thenReturn(1L);
        when(jwtUtil.extractEmail(anyString())).thenReturn("tester@example.com");
        SecuritySettingsResponse stub = new SecuritySettingsResponse();
        when(securityService.getSecuritySettings(anyLong())).thenReturn(stub);
        when(securityService.updateSecuritySettings(anyLong(), any(SecuritySettingsRequest.class))).thenReturn(stub);
    }

    @Test
    @DisplayName("GET /security/settings：2xx 且委托 getSecuritySettings")
    void getSettings_delegatesToService() throws Exception {
        mockMvc.perform(get("/api/v1/security/settings")
                        .header(HttpHeaders.AUTHORIZATION, AUTH))
                .andExpect(status().is2xxSuccessful());
        verify(securityService).getSecuritySettings(anyLong());
    }

    @Test
    @DisplayName("POST /security/settings：2xx 且委托 updateSecuritySettings")
    void updateSettings_delegatesToService() throws Exception {
        mockMvc.perform(post("/api/v1/security/settings")
                        .header(HttpHeaders.AUTHORIZATION, AUTH)
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().is2xxSuccessful());
        verify(securityService).updateSecuritySettings(anyLong(), any(SecuritySettingsRequest.class));
    }
}
