package com.orderservice.usecase.dto;

import lombok.Builder;

@Builder
public record CartDto(
    Long productId,
    String productName,
    Long price,
    String thumbnailUrl,
    Long quantity
) {

}
