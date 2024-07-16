package com.delivery.deliverycore.infrastructure.kafka.event;

import com.delivery.deliverycore.application.service.dto.ProcessDelivery;
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

    public ProcessDelivery mapToCommand() {
        return ProcessDelivery.builder()
            .productId(orderLine.productId())
            .productName(orderLine.productName())
            .quantity(orderLine.quantity())
            .orderLineId(orderLine.orderLineId())
            .deliveryAddressId(deliveryAddressId())
            .build();
    }

    public EventResult assignDeliveryIdAndStatus(Long deliveryId, int status) {
        return EventResult.builder()
            .productOrderId(productOrderId())
            .orderLine(orderLine())
            .memberId(memberId())
            .paymentMethodId(paymentMethodId())
            .deliveryAddressId(deliveryAddressId())
            .paymentId(paymentId())
            .deliveryId(deliveryId)
            .status(status)
            .build()
            ;
    }
}
