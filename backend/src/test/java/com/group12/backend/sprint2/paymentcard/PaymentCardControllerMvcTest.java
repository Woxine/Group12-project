package com.group12.backend.sprint2.paymentcard;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

import com.group12.backend.controller.PaymentCardController;
import com.group12.backend.dto.PaymentCardResponse;
import com.group12.backend.dto.StorePaymentCardRequest;
import com.group12.backend.service.PaymentCardService;
import com.group12.backend.utils.JwtUtil;

/**
 * ID2 控制器：断言与 {@link PaymentCardService} 已接线且端点返回 2xx。骨架阶段会失败。
 */
@WebMvcTest(controllers = PaymentCardController.class)
@DisplayName("ID2 PaymentCardController (WebMvc)")
class PaymentCardControllerMvcTest {

    private static final String AUTH = "Bearer unit-test-token";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentCardService paymentCardService;

    @MockBean
    private JwtUtil jwtUtil;

    @BeforeEach
    void stubJwtForProtectedApi() {
        when(jwtUtil.validateToken(anyString())).thenReturn(true);
        when(jwtUtil.extractUserId(anyString())).thenReturn(1L);
        when(jwtUtil.extractEmail(anyString())).thenReturn("tester@example.com");
        PaymentCardResponse stub = new PaymentCardResponse();
        when(paymentCardService.createCard(anyString(), any(StorePaymentCardRequest.class))).thenReturn(stub);
        when(paymentCardService.listCards(anyString())).thenReturn(java.util.List.of());
        when(paymentCardService.setDefaultCard(anyString(), anyString())).thenReturn(stub);
    }

    @Test
    @DisplayName("POST 创建卡片：2xx 且委托 PaymentCardService#createCard")
    void createCard_delegatesToService() throws Exception {
        String body = "{\"holderName\":\"Alice\",\"cardNumber\":\"4111111111111111\",\"brand\":\"VISA\",\"expiryMonth\":12,\"expiryYear\":2030}";
        mockMvc.perform(post("/api/v1/users/1/payment-cards")
                        .header(HttpHeaders.AUTHORIZATION, AUTH)
                        .requestAttr("userId", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().is2xxSuccessful());
        verify(paymentCardService).createCard(eq("1"), any(StorePaymentCardRequest.class));
    }

    @Test
    @DisplayName("GET 列表：2xx 且委托 PaymentCardService#listCards")
    void listCards_delegatesToService() throws Exception {
        mockMvc.perform(get("/api/v1/users/1/payment-cards")
                        .header(HttpHeaders.AUTHORIZATION, AUTH)
                        .requestAttr("userId", 1L))
                .andExpect(status().is2xxSuccessful());
        verify(paymentCardService).listCards("1");
    }

    @Test
    @DisplayName("DELETE 删除卡片：2xx 且委托 PaymentCardService#deleteCard")
    void deleteCard_delegatesToService() throws Exception {
        mockMvc.perform(delete("/api/v1/users/1/payment-cards/1")
                        .header(HttpHeaders.AUTHORIZATION, AUTH)
                        .requestAttr("userId", 1L))
                .andExpect(status().is2xxSuccessful());
        verify(paymentCardService).deleteCard("1", "1");
    }

    @Test
    @DisplayName("POST 设默认卡：2xx 且委托 PaymentCardService#setDefaultCard")
    void setDefaultCard_delegatesToService() throws Exception {
        mockMvc.perform(post("/api/v1/users/1/payment-cards/1/default")
                        .header(HttpHeaders.AUTHORIZATION, AUTH)
                        .requestAttr("userId", 1L))
                .andExpect(status().is2xxSuccessful());
        verify(paymentCardService).setDefaultCard("1", "1");
    }
}
