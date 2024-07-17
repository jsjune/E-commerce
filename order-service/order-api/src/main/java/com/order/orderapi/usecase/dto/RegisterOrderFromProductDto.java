package com.order.orderapi.usecase.dto;

public record RegisterOrderFromProductDto(
    Long productId,
    Long quantity
) {

}
