package com.productservice.usecase.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record ProductListResponseDto(
    List<ProductListDto> products,
    boolean hasNext
) {
}
