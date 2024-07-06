package com.orderservice.usecase.kafka.event;

import lombok.Builder;

@Builder
public record ProductInfoEvent(
    Long productId,
    Long quantity,
    String productName,
    Long price,
    String thumbnailUrl
) {

}
