package com.orderservice.usecase.dto;

import lombok.Builder;

@Builder
public record OrderRollbackDto(
    Long productOrderId,
    Long orderLineId,
    Long totalPrice,
    Long totalDiscount
) {

}
