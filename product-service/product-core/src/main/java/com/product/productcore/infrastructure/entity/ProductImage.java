package com.product.productcore.infrastructure.entity;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class ProductImage {
    private String orgImageUrl;
    private String orgImagePath;
    private String thumbnailUrl;
    private String thumbnailPath;
}
