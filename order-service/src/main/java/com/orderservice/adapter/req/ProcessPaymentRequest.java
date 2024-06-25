package com.orderservice.adapter.req;

import lombok.Builder;

@Builder
public record ProcessPaymentRequest(
    Long memberId,
    Long orderLineId,
    Long paymentMethodId,
    int totalPrice,
    int discount
) {

}
