package com.deliveryservice.usecase.dto;

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
