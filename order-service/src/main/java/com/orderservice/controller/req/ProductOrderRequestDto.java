package com.orderservice.controller.req;

import com.orderservice.usecase.dto.RegisterOrderOfProductDto;

public record ProductOrderRequestDto(
    Long productId,
    int quantity
) {
    public RegisterOrderOfProductDto mapToCommand() {
        return new RegisterOrderOfProductDto(productId, quantity);
    }
}
