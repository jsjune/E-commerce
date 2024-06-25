package com.memberservice.controller.internal.res;


import com.memberservice.entity.Cart;

public record CartDto(
    Long productId,
    String productName,
    int price,
    String thumbnailUrl,
    int quantity
) {

    public CartDto(Cart cart) {
        this(
            cart.getProductId(),
            cart.getProductName(),
            cart.getPrice(),
            cart.getThumbnailUrl(),
            cart.getQuantity()
        );
    }
}
