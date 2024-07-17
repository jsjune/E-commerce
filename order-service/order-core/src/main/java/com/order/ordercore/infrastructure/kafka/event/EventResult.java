package com.order.ordercore.infrastructure.kafka.event;

import com.order.ordercore.application.service.dto.OrderRollbackDto;
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
