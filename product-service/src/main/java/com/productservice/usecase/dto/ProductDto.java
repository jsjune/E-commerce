package com.productservice.usecase.dto;

import com.productservice.entity.Product;
import com.productservice.entity.ProductImage;
import java.io.Serializable;

public record ProductDto(Long productId, String productName, Long price, String thumbnailUrl) implements
    Serializable {
    public ProductDto(Product product) {
        this(
            product.getId(),
            product.getName(),
            product.getPrice(),
            product.getProductImages().stream()
                .map(ProductImage::getThumbnailUrl)
                .findFirst().orElse(null)
        );
    }
}
