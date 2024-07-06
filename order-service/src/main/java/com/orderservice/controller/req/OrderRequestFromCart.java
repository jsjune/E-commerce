package com.orderservice.controller.req;

import com.orderservice.usecase.dto.OrderDtoFromCart;
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
