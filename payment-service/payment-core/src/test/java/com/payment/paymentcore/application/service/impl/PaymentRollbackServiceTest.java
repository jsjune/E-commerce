package com.payment.paymentcore.application.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.payment.paymentcore.IntegrationTestSupport;
import com.payment.paymentcore.application.service.dto.RollbackPaymentDto;
import com.payment.paymentcore.infrastructure.entity.Payment;
import com.payment.paymentcore.infrastructure.entity.PaymentMethod;
import com.payment.paymentcore.infrastructure.entity.PaymentStatus;
import com.payment.paymentcore.infrastructure.entity.Refund;
import com.payment.paymentcore.infrastructure.entity.RefundStatus;
import com.payment.paymentcore.infrastructure.repository.PaymentMethodRepository;
import com.payment.paymentcore.infrastructure.repository.PaymentRepository;
import com.payment.paymentcore.infrastructure.repository.RefundRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PaymentRollbackServiceTest extends IntegrationTestSupport {
    @Autowired
    private PaymentRollbackService paymentRollbackService;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private RefundRepository refundRepository;
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @BeforeEach
    void setUp() {
        refundRepository.deleteAllInBatch();
        paymentRepository.deleteAllInBatch();
        paymentMethodRepository.deleteAllInBatch();
    }

    @DisplayName("결제 취소 진행 및 환불 요청")
    @Test
    void rollback_process_payment() throws Exception {
        // given
        PaymentMethod paymentMethod = PaymentMethod.builder().build();
        paymentMethodRepository.save(paymentMethod);
        Payment payment = Payment.builder().paymentMethod(paymentMethod).build();
        paymentRepository.save(payment);
        RollbackPaymentDto command = RollbackPaymentDto.builder()
            .paymentId(payment.getId())
            .build();

        // when
        paymentRollbackService.rollbackProcessPayment(command);
        Payment resultPayment = paymentRepository.findById(payment.getId()).get();
        Refund resultRefund = refundRepository.findAll().stream().findFirst().get();

        // then
        assertEquals(resultPayment.getPaymentStatus(), PaymentStatus.CANCELED);
        assertEquals(resultRefund.getRefundStatus(), RefundStatus.REFUND);
        assertNotNull(resultRefund.getReferenceCode());

    }
}
