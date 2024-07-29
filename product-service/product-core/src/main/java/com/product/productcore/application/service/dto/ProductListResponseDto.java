package com.product.productcore.application.service.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record ProductListResponseDto(
    List<ProductListDto> products,
    boolean hasNext
) {
}
