package com.payment.paymentcore.application.service.dto;

import lombok.Builder;

@Builder
public record ProcessPaymentDto(
    Long memberId,
    Long orderLineId,
    Long paymentMethodId,
    Long totalPrice,
    Long discount
) {

}
