package com.deliveryservice.usecase.kafka.event;

import lombok.Builder;

@Builder
public record OrderLineEvent(
    Long orderLineId,
    Long productId,
    String productName,
    Long price,
    Long discount,
    Long quantity
) {

}
