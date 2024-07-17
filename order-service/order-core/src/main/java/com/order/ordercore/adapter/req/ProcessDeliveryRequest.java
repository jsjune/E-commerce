package com.order.ordercore.adapter.req;

import lombok.Builder;

@Builder
public record ProcessDeliveryRequest(
    Long productId,
    String productName,
    Long quantity,
    Long orderLineId,
    Long deliveryAddressId) {

}
