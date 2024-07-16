package com.product.productapi.usecase;

import com.product.productapi.usecase.dto.ProductDto;

public interface InternalProductUseCase {
    int decreaseStock(Long productId, Long quantity);

    Boolean incrementStock(Long productId, Long quantity);

    ProductDto findProductById(Long productId);
}
