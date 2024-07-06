package com.orderservice.controller.req;

import com.orderservice.usecase.dto.RegisterOrderFromProductDto;

public record ProductOrderRequestDto(
    Long productId,
    Long quantity
) {
    public RegisterOrderFromProductDto mapToCommand() {
        return new RegisterOrderFromProductDto(productId, quantity);
    }
}
