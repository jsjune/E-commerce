package com.productservice.usecase;

import com.productservice.controller.req.ProductRequestDto;

public interface ProductWriteUseCase {
    void createProduct(Long memberId, ProductRequestDto request) throws Exception;

    Boolean decreaseStock(Long productId, int quantity);

    Boolean incrementStock(Long productId, int quantity);

}
