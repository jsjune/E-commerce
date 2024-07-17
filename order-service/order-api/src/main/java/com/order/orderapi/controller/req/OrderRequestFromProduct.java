package com.order.orderapi.controller.req;


import com.order.orderapi.usecase.dto.OrderDtoFromProduct;

public record OrderRequestFromProduct(
    Long paymentMethodId,
    Long deliveryAddressId,
    Long productId,
    Long quantity
) {
    public OrderDtoFromProduct mapToCommand() {
        return OrderDtoFromProduct.builder()
            .paymentMethodId(paymentMethodId)
            .deliveryAddressId(deliveryAddressId)
            .productId(productId)
            .quantity(quantity)
            .build();
    }
}
