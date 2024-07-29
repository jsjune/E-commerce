package com.order.ordercore.application.service.dto;

public record RegisterOrderFromProductDto(
    Long productId,
    Long quantity
) {

}
