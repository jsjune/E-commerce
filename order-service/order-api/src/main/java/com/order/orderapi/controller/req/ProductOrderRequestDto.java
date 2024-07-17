package com.order.orderapi.controller.req;


import com.order.orderapi.usecase.dto.RegisterOrderFromProductDto;

public record ProductOrderRequestDto(
    Long productId,
    Long quantity
) {
    public RegisterOrderFromProductDto mapToCommand() {
        return new RegisterOrderFromProductDto(productId, quantity);
    }
}
