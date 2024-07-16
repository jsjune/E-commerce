package com.orderservice.infrastructure.kafka.event;

import lombok.Builder;

@Builder
public record DeliveryEvent(
    Long deliveryAddressId,
    Long orderLineId,
    Long productId,
    String productName,
    Long quantity
) {

}
