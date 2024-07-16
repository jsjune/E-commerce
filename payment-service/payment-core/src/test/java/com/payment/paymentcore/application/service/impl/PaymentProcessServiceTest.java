package com.payment.paymentcore.application.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.payment.paymentcore.IntegrationTestSupport;
import com.payment.paymentcore.application.service.PaymentProcessUseCase;
import com.payment.paymentcore.application.service.dto.ProcessPaymentDto;
import com.payment.paymentcore.infrastructure.entity.Payment;
import com.payment.paymentcore.infrastructure.entity.PaymentMethod;
import com.payment.paymentcore.infrastructure.entity.PaymentStatus;
import com.payment.paymentcore.infrastructure.repository.PaymentMethodRepository;
import com.payment.paymentcore.infrastructure.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PaymentProcessServiceTest extends IntegrationTestSupport {
    @Autowired
    private PaymentProcessUseCase paymentProcessUseCase;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @BeforeEach
    void setUp() {
        paymentMethodRepository.deleteAllInBatch();
        paymentRepository.deleteAllInBatch();
    }

    @DisplayName("결제 진행 요청")
    @Test
    void process_payment() throws Exception {
        // given
        PaymentMethod paymentMethod = PaymentMethod.builder().build();
        paymentMethodRepository.save(paymentMethod);
        ProcessPaymentDto payment = ProcessPaymentDto.builder()
            .memberId(1L)
            .orderLineId(1L)
            .paymentMethodId(paymentMethod.getId())
            .totalPrice(6000L)
            .discount(0L)
            .build();

        // when
        paymentProcessUseCase.processPayment(payment);
        Payment result = paymentRepository.findAll().stream().findFirst().get();

        // then
        assertEquals(result.getMemberId(), payment.memberId());
        assertEquals(result.getOrderLineId(), payment.orderLineId());
        assertEquals(result.getPaymentMethod(), paymentMethod);
        assertEquals(result.getPaymentStatus(), PaymentStatus.COMPLETED);
        assertEquals(result.getTotalPrice(), 6000);
    }
}
