package com.orderservice.controller.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class OrderRequest {
    private Long orderId;
    private Long paymentMethodId;
    private Long deliveryAddressId;
}
