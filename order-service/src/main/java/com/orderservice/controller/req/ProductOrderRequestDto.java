package com.orderservice.controller.req;

import com.orderservice.usecase.dto.RegisterOrderOfProductDto;

public record ProductOrderRequestDto(
    Long productId,
    Long quantity
) {
    public RegisterOrderOfProductDto mapToCommand() {
        return new RegisterOrderOfProductDto(productId, quantity);
    }
}
