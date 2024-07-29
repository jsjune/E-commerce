package com.product.productcore.application.service.dto;

import java.io.Serializable;
import lombok.Builder;

@Builder
public record ProductDto(Long productId, String productName, Long price, String thumbnailUrl) implements
    Serializable {


}
