package com.orderservice.adapter.res;

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
