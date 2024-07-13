package com.paymentservice.usecase.kafka.event;

import com.paymentservice.usecase.dto.ProcessPaymentDto;
import lombok.Builder;

@Builder
public record ProductOrderEvent(
    Long productOrderId,
    OrderLineEvent orderLine,
    Long memberId,
    Long paymentMethodId,
    Long deliveryAddressId
) {
    public ProcessPaymentDto mapToCommand() {
        return ProcessPaymentDto.builder()
            .memberId(memberId())
            .orderLineId(orderLine.orderLineId())
            .totalPrice(orderLine.price() * orderLine.quantity())
            .discount(orderLine.discount())
            .paymentMethodId(paymentMethodId())
            .build();
    }

    public EventResult mapToEventResult(Long paymentId, int status) {
        return EventResult.builder()
            .productOrderId(productOrderId())
            .orderLine(orderLine())
            .memberId(memberId())
            .paymentMethodId(paymentMethodId())
            .deliveryAddressId(deliveryAddressId())
            .paymentId(paymentId)
            .status(status)
            .build();
    }

}
