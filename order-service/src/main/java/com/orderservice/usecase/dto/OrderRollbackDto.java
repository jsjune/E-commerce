package com.orderservice.usecase.dto;

import lombok.Builder;

@Builder
public record OrderRollbackDto(
    Long productOrderId,
    Long paymentId,
    Long deliveryId,
    Long orderLineId,
    Long totalPrice,
    Long totalDiscount
) {

}
