package com.paymentservice.controller.internal.req;

import com.paymentservice.usecase.dto.ProcessPayment;

public record ProcessPaymentRequest(
    Long memberId,
    Long orderLineId,
    Long paymentMethodId,
    int totalPrice,
    int discount
) {

    public ProcessPayment mapToCommand() {
        return ProcessPayment.builder()
            .memberId(memberId)
            .orderLineId(orderLineId)
            .paymentMethodId(paymentMethodId)
            .totalPrice(totalPrice)
            .discount(discount)
            .build();
    }
}
