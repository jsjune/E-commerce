package com.paymentservice.controller.internal.req;

import com.paymentservice.usecase.dto.ProcessPaymentDto;

public record ProcessPaymentRequest(
    Long memberId,
    Long orderLineId,
    Long paymentMethodId,
    Long totalPrice,
    Long discount
) {

    public ProcessPaymentDto mapToCommand() {
        return ProcessPaymentDto.builder()
            .memberId(memberId)
            .orderLineId(orderLineId)
            .paymentMethodId(paymentMethodId)
            .totalPrice(totalPrice)
            .discount(discount)
            .build();
    }
}
