package com.payment.paymentcore.infrastructure.kafka.event;

import com.payment.paymentcore.application.service.dto.RollbackPaymentDto;
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

    public RollbackPaymentDto mapToCommand() {
        return RollbackPaymentDto.builder()
            .paymentId(paymentId)
            .productName(orderLine.productName())
            .quantity(orderLine.quantity())
            .price(orderLine.price())
            .productId(orderLine.productId())
            .memberId(memberId())
            .build();
    }
}
