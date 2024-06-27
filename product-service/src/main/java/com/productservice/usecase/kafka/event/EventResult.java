package com.productservice.usecase.kafka.event;


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

    public EventResult withStatus(int status) {
        return EventResult.builder()
            .productOrderId(productOrderId())
            .orderLine(orderLine())
            .memberId(memberId())
            .paymentMethodId(paymentMethodId())
            .deliveryAddressId(deliveryAddressId())
            .paymentId(paymentId())
            .deliveryId(deliveryId())
            .status(status)
            .build();
    }
}
