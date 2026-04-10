package com.group12.backend.sprint2.bookingextension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import com.group12.backend.controller.BookingExtensionController;
import com.group12.backend.dto.ExtendBookingRequest;
import com.group12.backend.service.BookingExtensionService;
import com.group12.backend.utils.JwtUtil;

/**
 * ID10/11 延长接口：断言控制器已委托 {@link BookingExtensionService} 且返回 2xx；骨架阶段失败。
 */
@WebMvcTest(controllers = BookingExtensionController.class)
@DisplayName("ID10/11 BookingExtensionController (WebMvc)")
class BookingExtensionControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingExtensionService bookingExtensionService;

    @MockBean
    private JwtUtil jwtUtil;

    @BeforeEach
    void stubJwt() {
        when(jwtUtil.validateToken(anyString())).thenReturn(true);
        when(jwtUtil.extractUserId(anyString())).thenReturn(99L);
        when(jwtUtil.extractEmail(anyString())).thenReturn("tester@example.com");
        when(bookingExtensionService.extendBooking(anyString(), any(ExtendBookingRequest.class), anyLong()))
                .thenReturn(java.util.Map.of("ok", true));
    }

    @Test
    @DisplayName("PATCH /bookings/{id}/extend：2xx 且委托 extendBooking")
    void extendBooking_delegatesToService() throws Exception {
        mockMvc.perform(patch("/api/v1/bookings/b1/extend")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer t")
                        .requestAttr("userId", 99L)
                        .contentType(APPLICATION_JSON)
                        .content("{\"duration\":\"1H\"}"))
                .andExpect(status().is2xxSuccessful());
        verify(bookingExtensionService).extendBooking(eq("b1"), any(ExtendBookingRequest.class), eq(99L));
    }
}
