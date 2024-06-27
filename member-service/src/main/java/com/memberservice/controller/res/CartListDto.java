package com.memberservice.controller.res;

import lombok.Getter;

@Getter
public class CartListDto {

    private Long cartId;
    private Long productId;
    private String productName;
    private Long price;
    private Long quantity;
    private String thumbnailImageUrl;

    public CartListDto(Long cartId, Long productId, String productName, Long price, Long quantity,
        String imageUrl) {
        this.cartId = cartId;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.thumbnailImageUrl = imageUrl;
    }

}
