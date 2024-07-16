package com.orderservice.infrastructure.kafka.event;

import com.orderservice.usecase.dto.OrderRollbackDto;
import lombok.Builder;

@Builder
public record EventResult(
    Long productOrderId,
    OrderLineEvent orderLine,
    Long memberId,
    Long paymentMethodId,
    Long deliveryAddressId,
    Long paymentId,
    Long deliveryId,
    int status
) {

    public OrderRollbackDto mapToOrderRollbackDto() {
        return OrderRollbackDto.builder()
            .productOrderId(productOrderId())
            .productId(orderLine().productId())
            .quantity(orderLine().quantity())
            .paymentId(paymentId())
            .deliveryId(deliveryId())
            .orderLineId(orderLine().orderLineId())
            .totalPrice(orderLine().price() * orderLine().quantity())
            .totalDiscount(orderLine().discount())
            .build();
    }
}
