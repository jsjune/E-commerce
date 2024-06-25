package com.orderservice.adapter.res;

public record CartDto(
    Long productId,
    String productName,
    int price,
    String thumbnailUrl,
    int quantity
) {

}
