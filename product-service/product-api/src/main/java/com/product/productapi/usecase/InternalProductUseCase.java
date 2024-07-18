package com.product.productapi.usecase;

import com.product.productapi.usecase.dto.ProductDto;

public interface InternalProductUseCase {
    Boolean incrementStock(Long productId, Long quantity);

    ProductDto findProductById(Long productId);
}
