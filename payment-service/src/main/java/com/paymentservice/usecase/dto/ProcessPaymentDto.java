package com.paymentservice.usecase.dto;

import lombok.Builder;

@Builder
public record ProcessPaymentDto(
    Long memberId,
    Long orderLineId,
    Long paymentMethodId,
    int totalPrice,
    int discount
) {

}
