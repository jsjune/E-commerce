package com.order.ordercore.application.service.dto;

import lombok.Builder;

@Builder
public record OrderDtoFromProduct(
    Long paymentMethodId,
    Long deliveryAddressId,
    Long productId,
    Long quantity
) {

}
