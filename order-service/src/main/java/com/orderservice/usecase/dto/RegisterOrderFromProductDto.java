package com.orderservice.usecase.dto;

public record RegisterOrderFromProductDto(
    Long productId,
    Long quantity
) {

}
