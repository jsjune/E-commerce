package com.deliveryservice.controller.internal.req;

import com.deliveryservice.usecase.dto.ProcessDelivery;
import lombok.Builder;

@Builder
public record ProcessDeliveryRequest(
    Long productId,
    String productName,
    Long quantity,
    Long orderLineId,
    Long deliveryAddressId) {

    public ProcessDelivery mapToCommand() {
        return ProcessDelivery.builder()
            .productId(productId)
            .productName(productName)
            .quantity(quantity)
            .orderLineId(orderLineId)
            .deliveryAddressId(deliveryAddressId)
            .build();
    }
}
