package com.group12.backend.sprint3state1.guestbooking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.group12.backend.dto.BinLookupResponse;
import com.group12.backend.dto.PaymentCardResponse;
import com.group12.backend.dto.StorePaymentCardRequest;
import com.group12.backend.exception.BusinessException;
import com.group12.backend.exception.ErrorMessages;
import com.group12.backend.repository.PaymentCardRepository;
import com.group12.backend.repository.UserRepository;
import com.group12.backend.service.impl.PaymentCardBinLookupService;
import com.group12.backend.service.impl.PaymentCardServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("ID9 SalespersonBindCard")
class SalespersonBindCardTest {

    @Mock
    private PaymentCardRepository paymentCardRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PaymentCardBinLookupService paymentCardBinLookupService;

    @InjectMocks
    private PaymentCardServiceImpl paymentCardService;

    @Test
    @DisplayName("bindCard_success")
    void bindCard_success() {
        StorePaymentCardRequest request = new StorePaymentCardRequest();
        request.setHolderName("Guest User");
        request.setCardNumber("4111111111111111");
        request.setBrand("VISA");
        request.setExpiryMonth(12);
        request.setExpiryYear(2099);
        when(paymentCardBinLookupService.lookup("41111111", "guest-bind:10"))
                .thenReturn(buildLookup("VISA", "MATCHED"));

        assertThatCode(() -> paymentCardService.bindCardForGuest("G100", request, "10"))
                .doesNotThrowAnyException();
        PaymentCardResponse response = paymentCardService.bindCardForGuest("G100", request, "10");
        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("bindCard_rejectsBrandMismatch")
    void bindCard_rejectsBrandMismatch() {
        StorePaymentCardRequest request = new StorePaymentCardRequest();
        request.setHolderName("Guest User");
        request.setCardNumber("4111111111111111");
        request.setBrand("MASTERCARD");
        request.setExpiryMonth(12);
        request.setExpiryYear(2099);
        when(paymentCardBinLookupService.lookup("41111111", "guest-bind:10"))
                .thenReturn(buildLookup("VISA", "MATCHED"));

        assertThatThrownBy(() -> paymentCardService.bindCardForGuest("G100", request, "10"))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorMessages.PAYMENT_CARD_BRAND_MISMATCH);
    }

    private BinLookupResponse buildLookup(String brand, String status) {
        BinLookupResponse response = new BinLookupResponse();
        response.setBrand(brand);
        response.setIssuerBank("SIMULATED TEST ISSUER");
        response.setCardType("CREDIT");
        response.setCountryCode("GB");
        response.setStatus(status);
        return response;
    }
}
