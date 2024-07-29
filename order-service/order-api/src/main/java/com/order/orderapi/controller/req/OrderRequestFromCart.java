package com.order.orderapi.controller.req;

import com.order.ordercore.application.service.dto.OrderDtoFromCart;
import java.util.List;

public record OrderRequestFromCart(
    List<Long> cartIds,
    Long paymentMethodId,
    Long deliveryAddressId
) {
    public OrderDtoFromCart mapToCommand() {
        return OrderDtoFromCart.builder()
            .cartIds(cartIds)
            .paymentMethodId(paymentMethodId)
            .deliveryAddressId(deliveryAddressId)
            .build();
    }

}
