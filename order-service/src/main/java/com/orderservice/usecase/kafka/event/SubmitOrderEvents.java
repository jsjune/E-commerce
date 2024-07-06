package com.orderservice.usecase.kafka.event;

import java.util.List;
import lombok.Builder;

@Builder
public record SubmitOrderEvents(
    Long memberId,
    Long paymentMethodId,
    Long deliveryAddressId,
    List<ProductInfoEvent> productInfo
) {

}
