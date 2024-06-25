package com.orderservice.usecase.dto;

import lombok.Builder;

@Builder
public record OrderDto(
    Long orderId,
    Long paymentMethodId,
    Long deliveryAddressId
) {

}
