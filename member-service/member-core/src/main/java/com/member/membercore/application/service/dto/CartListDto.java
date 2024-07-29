package com.member.membercore.application.service.dto;

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
