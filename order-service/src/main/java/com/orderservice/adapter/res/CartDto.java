package com.orderservice.adapter.res;

public record CartDto(
    Long productId,
    String productName,
    Long price,
    String thumbnailUrl,
    Long quantity
) {

}
