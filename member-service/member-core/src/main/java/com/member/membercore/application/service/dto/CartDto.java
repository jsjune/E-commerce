package com.member.membercore.application.service.dto;

import com.ecommerce.common.cache.CachingCartListDto;

public record CartDto(
    Long productId,
    String productName,
    Long price,
    String thumbnailUrl,
    Long quantity
) {

    public CartDto(CachingCartListDto cachingCartListDto) {
        this(
            cachingCartListDto.productId(),
            cachingCartListDto.productName(),
            cachingCartListDto.price(),
            cachingCartListDto.thumbnailImageUrl(),
            cachingCartListDto.quantity()
        );
    }
}
