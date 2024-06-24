package com.productservice.usecase;

import com.productservice.controller.req.ProductRequestDto;

public interface ProductWriteUseCase {
    void createProduct(Long memberId, ProductRequestDto request) throws Exception;

    void decreaseStock(Long productId, int quantity);

    void incrementStock(Long productId, int quantity);

}
