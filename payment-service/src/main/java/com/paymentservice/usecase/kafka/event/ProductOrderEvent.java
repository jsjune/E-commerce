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
            .orderLineId(orderLine.orderLineId())
            .totalPrice(orderLine.price() * orderLine.quantity())
            .discount(orderLine.discount())
            .memberId(memberId())
            .paymentMethodId(paymentMethodId())
            .build();
    }

}
