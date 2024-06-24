package com.memberservice.controller.res;

import lombok.Getter;

@Getter
public class CartListDto {

    private Long cartId;
    private Long productId;
    private String productName;
    private int price;
    private int quantity;
    private String thumbnailImageUrl;

    public CartListDto(Long cartId, Long productId, String productName, int price, int quantity,
        String imageUrl) {
        this.cartId = cartId;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.thumbnailImageUrl = imageUrl;
    }

}
