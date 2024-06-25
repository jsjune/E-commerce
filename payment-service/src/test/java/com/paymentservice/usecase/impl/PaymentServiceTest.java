package com.paymentservice.usecase.impl;


import static org.junit.jupiter.api.Assertions.assertEquals;

import com.paymentservice.IntegrationTestSupport;
import com.paymentservice.entity.Payment;
import com.paymentservice.entity.PaymentMethod;
import com.paymentservice.entity.PaymentStatus;
import com.paymentservice.repository.PaymentMethodRepository;
import com.paymentservice.repository.PaymentRepository;
import com.paymentservice.usecase.PaymentUseCase;
import com.paymentservice.usecase.dto.ProcessPayment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PaymentServiceTest extends IntegrationTestSupport {

    @Autowired
    private PaymentUseCase paymentUseCase;
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
        ProcessPayment payment = ProcessPayment.builder()
            .memberId(1L)
            .orderLineId(1L)
            .paymentMethodId(paymentMethod.getId())
            .totalPrice(6000)
            .discount(0)
            .build();

        // when
        paymentUseCase.processPayment(payment);
        Payment result = paymentRepository.findAll().stream().findFirst().get();

        // then
        assertEquals(result.getMemberId(), payment.memberId());
        assertEquals(result.getOrderLineId(), payment.orderLineId());
        assertEquals(result.getPaymentMethod(), paymentMethod);
        assertEquals(result.getPaymentStatus(), PaymentStatus.COMPLETED);
        assertEquals(result.getTotalPrice(), 6000);
    }

}
