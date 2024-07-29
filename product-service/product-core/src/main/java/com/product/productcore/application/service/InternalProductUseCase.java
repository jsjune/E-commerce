package com.product.productcore.application.service;


import com.product.productcore.application.service.dto.ProductDto;

public interface InternalProductUseCase {
    Boolean incrementStock(Long productId, Long quantity);

    ProductDto findProductById(Long productId);
}
