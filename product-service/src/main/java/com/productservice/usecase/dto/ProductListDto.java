package com.productservice.usecase.dto;

import java.util.List;
import java.util.Set;
import lombok.Builder;

@Builder
public record ProductListDto(
    Long productId,
    Long sellerId,
    String company,
    String phoneNumber,
    String name,
    String description,
    Long price,
    Set<String> tags,
    List<String> orgProductImages
) {
}
