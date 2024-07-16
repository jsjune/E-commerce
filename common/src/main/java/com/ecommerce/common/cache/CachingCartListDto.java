package com.ecommerce.common.cache;


import java.io.Serializable;
import lombok.Builder;

@Builder
public record CachingCartListDto(
    Long cartId,
    Long productId,
    String productName,
    Long price,
    Long quantity,
    String thumbnailImageUrl
) implements Serializable {

}
