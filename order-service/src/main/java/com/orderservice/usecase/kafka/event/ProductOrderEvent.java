package com.orderservice.usecase.kafka.event;

import lombok.Builder;

@Builder
public record ProductOrderEvent(
    Long productOrderId,
    OrderLineEvent orderLine,
    Long memberId,
    Long paymentMethodId,
    Long deliveryAddressId
) {

}
