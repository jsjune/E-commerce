package com.memberservice.controller.internal.res;


import com.ecommerce.common.cache.CartListDto;
import com.memberservice.entity.Cart;

public record CartDto(
    Long productId,
    String productName,
    Long price,
    String thumbnailUrl,
    Long quantity
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

    public CartDto(CartListDto cartListDto) {
        this(
            cartListDto.productId(),
            cartListDto.productName(),
            cartListDto.price(),
            cartListDto.thumbnailImageUrl(),
            cartListDto.quantity()
        );
    }
}
