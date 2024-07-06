package com.orderservice.usecase.kafka.event;

import lombok.Builder;

@Builder
public record SubmitOrderEvent(
    Long memberId,
    Long paymentMethodId,
    Long deliveryAddressId,
    Long quantity,
    Long productId,
    String productName,
    Long price,
    String thumbnailUrl
) {

}
