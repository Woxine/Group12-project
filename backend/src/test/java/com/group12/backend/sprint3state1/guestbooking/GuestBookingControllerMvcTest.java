package com.group12.backend.sprint3state1.guestbooking;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
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

import com.group12.backend.controller.BookingController;
import com.group12.backend.dto.CreateGuestBookingRequest;
import com.group12.backend.service.BookingService;
import com.group12.backend.utils.JwtUtil;

@WebMvcTest(controllers = BookingController.class)
@DisplayName("ID9 GuestBookingController")
class GuestBookingControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        when(jwtUtil.validateToken(anyString())).thenReturn(true);
        when(jwtUtil.extractUserId(anyString())).thenReturn(10L);
        when(jwtUtil.extractEmail(anyString())).thenReturn("staff@example.com");
        when(bookingService.createGuestBooking(any(CreateGuestBookingRequest.class)))
                .thenReturn(java.util.Map.of("id", "B100"));
    }

    @Test
    @DisplayName("createGuestBooking_returns201")
    void createGuestBooking_returns201() throws Exception {
        String payload = """
                {
                  "salespersonId":"10",
                  "guestId":"G100",
                  "guestName":"Guest A",
                  "guestContact":"guest@example.com",
                  "scooterId":"1",
                  "duration":"1H"
                }
                """;

        mockMvc.perform(post("/api/v1/bookings/guest")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer test-token")
                        .contentType(APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated());

        verify(bookingService).createGuestBooking(any(CreateGuestBookingRequest.class));
    }
}
