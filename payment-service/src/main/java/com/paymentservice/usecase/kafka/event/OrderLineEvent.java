package com.paymentservice.usecase.kafka.event;

import lombok.Builder;

@Builder
public record OrderLineEvent(
    Long orderLineId,
    Long productId,
    String productName,
    Long price,
    Long quantity,
    Long discount
) {

}
