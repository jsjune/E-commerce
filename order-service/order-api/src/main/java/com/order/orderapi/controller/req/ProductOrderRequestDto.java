package com.order.orderapi.controller.req;


import com.order.ordercore.application.service.dto.RegisterOrderFromProductDto;

public record ProductOrderRequestDto(
    Long productId,
    Long quantity
) {
    public RegisterOrderFromProductDto mapToCommand() {
        return new RegisterOrderFromProductDto(productId, quantity);
    }
}
