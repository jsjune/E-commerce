package com.orderservice.controller.req;

import com.orderservice.usecase.dto.OrderDto;

public record OrderRequest(
    Long orderId,
    Long paymentMethodId,
    Long deliveryAddressId
) {
    public OrderDto mapToCommand() {
        return OrderDto.builder()
            .orderId(orderId)
            .paymentMethodId(paymentMethodId)
            .deliveryAddressId(deliveryAddressId)
            .build();
    }
}
