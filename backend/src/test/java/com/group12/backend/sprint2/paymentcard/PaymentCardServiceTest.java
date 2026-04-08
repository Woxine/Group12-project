package com.group12.backend.sprint2.paymentcard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.group12.backend.dto.PaymentCardResponse;
import com.group12.backend.dto.StorePaymentCardRequest;
import com.group12.backend.service.impl.PaymentCardServiceImpl;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * ID2 支付卡服务：断言<strong>实现完成后</strong>的契约。骨架阶段会失败，迫使 CI 保持红色直到实现落地。
 */
@DisplayName("ID2 PaymentCardService")
class PaymentCardServiceTest {

    private PaymentCardServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PaymentCardServiceImpl();
    }

    @Test
    @DisplayName("createCard：成功时返回非空响应且不抛异常")
    void createCard_returnsResponse_whenValidInput() {
        var req = new StorePaymentCardRequest();
        req.setHolderName("Alice");
        req.setCardNumber("4111111111111111");
        assertThatCode(() -> service.createCard("1", req)).doesNotThrowAnyException();
        PaymentCardResponse r = service.createCard("1", req);
        assertThat(r).isNotNull();
    }

    @Test
    @DisplayName("listCards：成功时返回列表（可为空）且不抛异常")
    void listCards_returnsList_withoutThrowing() {
        assertThatCode(() -> service.listCards("1")).doesNotThrowAnyException();
        List<PaymentCardResponse> list = service.listCards("1");
        assertThat(list).isNotNull();
    }

    @Test
    @DisplayName("deleteCard：成功时正常返回且不抛异常")
    void deleteCard_completes_withoutThrowing() {
        assertThatCode(() -> service.deleteCard("1", "card-1")).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("setDefaultCard：成功时返回非空响应且不抛异常")
    void setDefaultCard_returnsResponse_whenValid() {
        assertThatCode(() -> service.setDefaultCard("1", "card-1")).doesNotThrowAnyException();
        PaymentCardResponse r = service.setDefaultCard("1", "card-1");
        assertThat(r).isNotNull();
    }

    @Nested
    @DisplayName("StorePaymentCardRequest Bean Validation")
    class StorePaymentCardRequestValidation {

        private Validator validator;

        @BeforeEach
        void initValidator() {
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            validator = factory.getValidator();
        }

        @Test
        @DisplayName("holderName、cardNumber 为空时校验失败")
        void invalid_when_requiredFieldsBlank() {
            StorePaymentCardRequest req = new StorePaymentCardRequest();
            Set<ConstraintViolation<StorePaymentCardRequest>> violations = validator.validate(req);
            assertThat(violations).isNotEmpty();
            assertThat(violations.stream().map(ConstraintViolation::getPropertyPath).map(Object::toString))
                    .containsExactlyInAnyOrder("holderName", "cardNumber");
        }

        @Test
        @DisplayName("必填字段合法时无约束违反")
        void valid_when_requiredFieldsPresent() {
            StorePaymentCardRequest req = new StorePaymentCardRequest();
            req.setHolderName("Alice");
            req.setCardNumber("4111111111111111");
            assertThat(validator.validate(req)).isEmpty();
        }
    }
}
