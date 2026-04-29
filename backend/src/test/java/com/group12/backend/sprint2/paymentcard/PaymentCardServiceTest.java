package com.group12.backend.sprint2.paymentcard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.group12.backend.dto.PaymentCardResponse;
import com.group12.backend.dto.BinLookupResponse;
import com.group12.backend.dto.StorePaymentCardRequest;
import com.group12.backend.entity.PaymentCard;
import com.group12.backend.entity.User;
import com.group12.backend.repository.PaymentCardRepository;
import com.group12.backend.repository.UserRepository;
import com.group12.backend.service.impl.PaymentCardBinLookupService;
import com.group12.backend.service.impl.PaymentCardServiceImpl;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * ID2 支付卡服务：断言<strong>实现完成后</strong>的契约。骨架阶段会失败，迫使 CI 保持红色直到实现落地。
 */
@DisplayName("ID2 PaymentCardService")
@ExtendWith(MockitoExtension.class)
class PaymentCardServiceTest {

    @Mock
    private PaymentCardRepository paymentCardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentCardBinLookupService paymentCardBinLookupService;

    @InjectMocks
    private PaymentCardServiceImpl service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "paymentCardBinLookupService", paymentCardBinLookupService);
    }

    @Test
    @DisplayName("createCard：成功时返回非空响应且不抛异常")
    void createCard_returnsResponse_whenValidInput() {
        StorePaymentCardRequest req = buildValidRequest();
        User user = buildUser(1L);
        PaymentCard saved = buildCard(101L, user, true, LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(paymentCardRepository.existsByUser_IdAndBrandIgnoreCaseAndLast4AndExpiryMonthAndExpiryYear(
                1L, "VISA", "1111", 12, 2030)).thenReturn(false);
        when(paymentCardRepository.findByUser_IdAndIsDefaultTrue(1L)).thenReturn(Optional.empty());
        when(paymentCardRepository.save(any(PaymentCard.class))).thenReturn(saved);

        PaymentCardResponse r = service.createCard("1", req);
        assertThat(r).isNotNull();
        assertThat(r.getId()).isEqualTo("101");
        assertThat(r.getMaskedNumber()).isEqualTo("**** **** **** 1111");
        assertThat(r.getIsDefault()).isTrue();
    }

    @Test
    @DisplayName("listCards：成功时返回列表（可为空）且不抛异常")
    void listCards_returnsList_withoutThrowing() {
        User user = buildUser(1L);
        PaymentCard nonDefaultCard = buildCard(201L, user, false, LocalDateTime.now());
        PaymentCard defaultCard = buildCard(202L, user, true, LocalDateTime.now().minusDays(1));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(paymentCardRepository.findByUser_IdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(nonDefaultCard, defaultCard));

        List<PaymentCardResponse> list = service.listCards("1");
        assertThat(list).isNotNull();
        assertThat(list).hasSize(2);
        assertThat(list.get(0).getId()).isEqualTo("202");
        assertThat(list.get(0).getIsDefault()).isTrue();
    }

    @Test
    @DisplayName("deleteCard：成功时正常返回且不抛异常")
    void deleteCard_completes_withoutThrowing() {
        User user = buildUser(1L);
        PaymentCard targetDefaultCard = buildCard(301L, user, true, LocalDateTime.now());
        PaymentCard remainingCard = buildCard(302L, user, false, LocalDateTime.now().minusDays(1));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(paymentCardRepository.findByIdAndUser_Id(301L, 1L)).thenReturn(Optional.of(targetDefaultCard));
        when(paymentCardRepository.findByUser_IdOrderByCreatedAtDesc(1L)).thenReturn(List.of(remainingCard));

        service.deleteCard("1", "301");
        verify(paymentCardRepository).delete(targetDefaultCard);
        verify(paymentCardRepository).save(any(PaymentCard.class));
    }

    @Test
    @DisplayName("setDefaultCard：成功时返回非空响应且不抛异常")
    void setDefaultCard_returnsResponse_whenValid() {
        User user = buildUser(1L);
        PaymentCard targetCard = buildCard(401L, user, false, LocalDateTime.now());
        PaymentCard oldDefaultCard = buildCard(402L, user, true, LocalDateTime.now().minusDays(2));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(paymentCardRepository.findByIdAndUser_Id(401L, 1L)).thenReturn(Optional.of(targetCard));
        when(paymentCardRepository.findByUser_IdAndIsDefaultTrue(1L)).thenReturn(Optional.of(oldDefaultCard));
        when(paymentCardRepository.save(any(PaymentCard.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentCardResponse r = service.setDefaultCard("1", "401");
        assertThat(r).isNotNull();
        assertThat(r.getId()).isEqualTo("401");
        assertThat(r.getIsDefault()).isTrue();
    }

    @Test
    @DisplayName("lookupCardBin：服务可用时返回查询结果")
    void lookupCardBin_returnsProviderResult_whenLookupServiceAvailable() {
        User user = buildUser(1L);
        BinLookupResponse lookup = new BinLookupResponse();
        lookup.setBrand("VISA");
        lookup.setStatus("MATCHED");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(paymentCardBinLookupService.lookup("411111", "1:127.0.0.1")).thenReturn(lookup);

        BinLookupResponse response = service.lookupCardBin("1", "411111", "1:127.0.0.1");
        assertThat(response).isNotNull();
        assertThat(response.getBrand()).isEqualTo("VISA");
        assertThat(response.getStatus()).isEqualTo("MATCHED");
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
                    .containsExactlyInAnyOrder("holderName", "cardNumber", "brand");
        }

        @Test
        @DisplayName("必填字段合法时无约束违反")
        void valid_when_requiredFieldsPresent() {
            StorePaymentCardRequest req = new StorePaymentCardRequest();
            req.setHolderName("Alice");
            req.setCardNumber("4111111111111111");
            req.setBrand("VISA");
            req.setExpiryMonth(12);
            req.setExpiryYear(2030);
            assertThat(validator.validate(req)).isEmpty();
        }
    }

    private StorePaymentCardRequest buildValidRequest() {
        StorePaymentCardRequest req = new StorePaymentCardRequest();
        req.setHolderName("Alice");
        req.setCardNumber("4111111111111111");
        req.setBrand("VISA");
        req.setExpiryMonth(12);
        req.setExpiryYear(2030);
        return req;
    }

    private User buildUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setEmail("alice@example.com");
        user.setName("Alice");
        user.setPassword("encoded");
        user.setRole("CUSTOMER");
        return user;
    }

    private PaymentCard buildCard(Long id, User user, boolean isDefault, LocalDateTime createdAt) {
        PaymentCard card = new PaymentCard();
        card.setId(id);
        card.setUser(user);
        card.setHolderName("Alice");
        card.setBrand("VISA");
        card.setLast4("1111");
        card.setExpiryMonth(12);
        card.setExpiryYear(2030);
        card.setIsDefault(isDefault);
        card.setCreatedAt(createdAt);
        return card;
    }
}
