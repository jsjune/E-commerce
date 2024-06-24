package com.orderservice.payment.usecase;


import com.orderservice.order.entity.OrderLine;
import com.orderservice.payment.entity.Payment;

public interface PaymentUseCase {

    Payment processPayment(Long memberId, OrderLine orderLine, Long paymentMethodId)
        throws Exception;
}
