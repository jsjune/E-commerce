package com.orderservice.usecase.dto;

public record RegisterOrderOfProductDto(
    Long productId,
    Long quantity
) {

}
