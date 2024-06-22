package com.ecommerce.order.controller.req;

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
