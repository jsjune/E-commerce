package com.payment.paymentcore.application.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.payment.paymentapi.usecase.PaymentMethodUseCase;
import com.payment.paymentapi.usecase.dto.PaymentMethodResponseDto;
import com.payment.paymentapi.usecase.dto.RegisterPaymentMethodDto;
import com.payment.paymentcore.IntegrationTestSupport;
import com.payment.paymentcore.application.utils.AesUtil;
import com.payment.paymentcore.infrastructure.entity.PaymentMethod;
import com.payment.paymentcore.infrastructure.entity.PaymentType;
import com.payment.paymentcore.infrastructure.repository.PaymentMethodRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PaymentMethodServiceTest extends IntegrationTestSupport {
    @Autowired
    private PaymentMethodUseCase paymentMethodUseCase;
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
    @Autowired
    private AesUtil aesUtil;

    @BeforeEach
    public void setUp() {
        paymentMethodRepository.deleteAllInBatch();
    }

    @DisplayName("결제 수단 조회")
    @Test
    void get_payment_methods() throws Exception {
        // given
        Long memberId = 1L;
        String bank = "bank";
        RegisterPaymentMethodDto command = getPaymentMethodRequestDto(bank);

        // when
        paymentMethodUseCase.registerPaymentMethod(memberId, command);
        paymentMethodUseCase.registerPaymentMethod(memberId, command);
        PaymentMethodResponseDto result = paymentMethodUseCase.getPaymentMethods(
            memberId);

        // then
        assertEquals(result.paymentMethods().size(), 2);
        assertEquals(result.paymentMethods().get(0).bank(), bank);

    }

    @DisplayName("결제 수단 등록 성공")
    @Test
    void register_paymentMethod() throws Exception {
        // given
        Long memberId = 1L;
        RegisterPaymentMethodDto command = getPaymentMethodRequestDto("bank");

        // when
        paymentMethodUseCase.registerPaymentMethod(memberId, command);
        PaymentMethod result = paymentMethodRepository.findAll().stream().findFirst()
            .orElse(null);

        // then
        assertEquals(result.getPaymentType().name(), command.paymentType());
        assertEquals(aesUtil.aesDecode(result.getCreditCardNumber()), command.creditCardNumber());
    }

    private static RegisterPaymentMethodDto getPaymentMethodRequestDto(String bank) {
        return RegisterPaymentMethodDto.builder()
            .paymentType(PaymentType.CREDIT_CARD.name())
            .creditCardNumber("1234-1234-1234-1234")
            .bank(bank)
            .accountNumber(null)
            .build();
    }
}
