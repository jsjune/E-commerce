package com.delivery.deliverycore.application.service.dto;

import lombok.Builder;

@Builder
public record ProcessDelivery(
    Long productId,
    String productName,
    Long quantity,
    Long orderLineId,
    Long deliveryAddressId
) {
}
