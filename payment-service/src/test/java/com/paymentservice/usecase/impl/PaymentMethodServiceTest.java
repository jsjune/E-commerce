package com.paymentservice.usecase.impl;


import static org.junit.jupiter.api.Assertions.assertEquals;

import com.paymentservice.IntegrationTestSupport;
import com.paymentservice.controller.req.PaymentMethodRequestDto;
import com.paymentservice.controller.res.PaymentMethodResponseDto;
import com.paymentservice.entity.PaymentMethod;
import com.paymentservice.entity.PaymentType;
import com.paymentservice.repository.PaymentMethodRepository;
import com.paymentservice.usecase.PaymentMethodUseCase;
import com.paymentservice.usecase.dto.RegisterPaymentMethodDto;
import com.paymentservice.utils.AesUtil;
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
        assertEquals(result.getPaymentMethods().size(), 2);
        assertEquals(result.getPaymentMethods().get(0).getBank(), bank);

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
        assertEquals(result.getPaymentType(), command.paymentType());
        assertEquals(aesUtil.aesDecode(result.getCreditCardNumber()), command.creditCardNumber());
    }

    private static RegisterPaymentMethodDto getPaymentMethodRequestDto(String bank) {
        return RegisterPaymentMethodDto.builder()
            .paymentType(PaymentType.CREDIT_CARD)
            .creditCardNumber("1234-1234-1234-1234")
            .bank(bank)
            .accountNumber(null)
            .build();
    }
}
