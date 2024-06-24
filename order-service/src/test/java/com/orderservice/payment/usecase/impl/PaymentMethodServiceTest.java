package com.orderservice.payment.usecase.impl;


import static org.junit.jupiter.api.Assertions.assertEquals;

import com.orderservice.IntegrationTestSupport;
import com.orderservice.adapter.dto.MemberDto;
import com.orderservice.payment.controller.req.PaymentMethodRequestDto;
import com.orderservice.payment.controller.res.PaymentMethodResponseDto;
import com.orderservice.payment.entity.PaymentMethod;
import com.orderservice.payment.entity.PaymentType;
import com.orderservice.payment.repository.PaymentMethodRepository;
import com.orderservice.payment.usecase.PaymentMethodUseCase;
import com.orderservice.utils.AesUtil;
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
        MemberDto member = new MemberDto(1L, "010-1234-5678", "회사");
        String bank = "bank";
        PaymentMethodRequestDto request = getPaymentMethodRequestDto(bank);

        // when
        paymentMethodUseCase.registerPaymentMethod(member.memberId(), request);
        paymentMethodUseCase.registerPaymentMethod(member.memberId(), request);
        PaymentMethodResponseDto result = paymentMethodUseCase.getPaymentMethods(
            member.memberId());

        // then
        assertEquals(result.getPaymentMethods().size(), 2);
        assertEquals(result.getPaymentMethods().get(0).getBank(), bank);

    }

    @DisplayName("결제 수단 등록 성공")
    @Test
    void register_paymentMethod() throws Exception {
        // given
        MemberDto member = new MemberDto(1L, "010-1234-5678", "회사");
        PaymentMethodRequestDto request = getPaymentMethodRequestDto("bank");

        // when
        paymentMethodUseCase.registerPaymentMethod(member.memberId(), request);
        PaymentMethod result = paymentMethodRepository.findAll().stream().findFirst()
            .orElse(null);

        // then
        assertEquals(result.getPaymentType(), request.getPaymentType());
        assertEquals(aesUtil.aesDecode(result.getCreditCardNumber()), request.getCreditCardNumber());
    }

    private static PaymentMethodRequestDto getPaymentMethodRequestDto(String bank) {
        return PaymentMethodRequestDto.builder()
            .paymentType(PaymentType.CREDIT_CARD)
            .creditCardNumber("1234-1234-1234-1234")
            .bank(bank)
            .accountNumber(null)
            .build();
    }
}
