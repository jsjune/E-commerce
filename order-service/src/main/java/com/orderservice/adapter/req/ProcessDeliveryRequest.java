package com.orderservice.adapter.req;

import lombok.Builder;

@Builder
public record ProcessDeliveryRequest(
    Long productId,
    String productName,
    int quantity,
    Long orderLineId,
    Long deliveryAddressId) {

}
