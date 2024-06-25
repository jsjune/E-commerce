package com.productservice.controller.internal.res;

import com.productservice.entity.Product;
import com.productservice.entity.ProductImage;

public record ProductDto(Long productId, String productName, int price, String thumbnailUrl) {
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
