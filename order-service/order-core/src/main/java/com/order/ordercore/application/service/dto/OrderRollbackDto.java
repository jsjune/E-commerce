package com.order.ordercore.application.service.dto;

import lombok.Builder;

@Builder
public record OrderRollbackDto(
    Long productOrderId,
    Long productId,
    Long quantity,
    Long paymentId,
    Long deliveryId,
    Long orderLineId,
    Long totalPrice,
    Long totalDiscount
) {

}
