package com.ecommerce.payment.usecase.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.ecommerce.IntegrationTestSupport;
import com.ecommerce.common.AesUtil;
import com.ecommerce.member.entity.Member;
import com.ecommerce.member.repository.MemberRepository;
import com.ecommerce.payment.controller.req.PaymentMethodRequestDto;
import com.ecommerce.payment.controller.res.PaymentMethodResponseDto;
import com.ecommerce.payment.entity.PaymentMethod;
import com.ecommerce.payment.entity.PaymentType;
import com.ecommerce.payment.repository.PaymentMethodRepository;
import com.ecommerce.payment.usecase.PaymentMethodUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PaymentMethodServiceTest extends IntegrationTestSupport {

    @Autowired
    private PaymentMethodUseCase paymentMethodUseCase;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
    @Autowired
    private AesUtil aesUtil;

    @BeforeEach
    public void setUp() {
        paymentMethodRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("결제 수단 조회")
    @Test
    void get_payment_methods() throws Exception {
        // given
        Member member = Member.builder().build();
        memberRepository.save(member);
        String bank = "bank";
        PaymentMethodRequestDto request = getPaymentMethodRequestDto(bank);

        // when
        paymentMethodUseCase.registerPaymentMethod(member.getId(), request);
        paymentMethodUseCase.registerPaymentMethod(member.getId(), request);
        PaymentMethodResponseDto result = paymentMethodUseCase.getPaymentMethods(
            member.getId());

        // then
        assertEquals(result.getPaymentMethods().size(), 2);
        assertEquals(result.getPaymentMethods().get(0).getBank(), bank);

    }

    @DisplayName("결제 수단 등록 성공")
    @Test
    void register_paymentMethod() throws Exception {
        // given
        Member member = Member.builder().build();
        memberRepository.save(member);
        PaymentMethodRequestDto request = getPaymentMethodRequestDto("bank");

        // when
        paymentMethodUseCase.registerPaymentMethod(member.getId(), request);
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
