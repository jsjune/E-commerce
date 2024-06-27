package com.orderservice.adapter.req;

import lombok.Builder;

@Builder
public record ProcessPaymentRequest(
    Long memberId,
    Long orderLineId,
    Long paymentMethodId,
    Long totalPrice,
    Long discount
) {

}
