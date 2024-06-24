package com.orderservice.adapter.dto;

public record CartDto(
    Long productId,
    String productName,
    int price,
    String thumbnailUrl,
    int quantity
) {

}
