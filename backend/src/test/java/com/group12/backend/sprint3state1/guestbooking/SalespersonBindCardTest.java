package com.group12.backend.sprint3state1.guestbooking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.group12.backend.dto.PaymentCardResponse;
import com.group12.backend.dto.StorePaymentCardRequest;
import com.group12.backend.repository.PaymentCardRepository;
import com.group12.backend.repository.UserRepository;
import com.group12.backend.service.impl.PaymentCardServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("ID9 SalespersonBindCard")
class SalespersonBindCardTest {

    @Mock
    private PaymentCardRepository paymentCardRepository;
    @Mock
    private UserRepository userRepository;

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

        assertThatCode(() -> paymentCardService.bindCardForGuest("G100", request, "10"))
                .doesNotThrowAnyException();
        PaymentCardResponse response = paymentCardService.bindCardForGuest("G100", request, "10");
        assertThat(response).isNotNull();
    }
}
