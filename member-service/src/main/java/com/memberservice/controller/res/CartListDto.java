package com.memberservice.controller.res;

import com.memberservice.controller.internal.res.CartDto;
import com.memberservice.entity.Cart;
import java.io.Serializable;
import lombok.Builder;

@Builder
public record CartListDto (
    Long cartId,
    Long productId,
    String productName,
    Long price,
    Long quantity,
    String thumbnailImageUrl
) implements Serializable {

    public static CartListDto of(Cart cart) {
        return CartListDto.builder()
            .cartId(cart.getId())
            .productId(cart.getProductId())
            .productName(cart.getProductName())
            .price(cart.getPrice())
            .quantity(cart.getQuantity())
            .thumbnailImageUrl(cart.getThumbnailUrl())
            .build();
    }

    public CartDto toCartDto() {
        return new CartDto(
            productId(),
            productName(),
            price(),
            thumbnailImageUrl(),
            quantity()
        );
    }

}
