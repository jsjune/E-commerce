package com.productservice.usecase;

import com.productservice.usecase.dto.RegisterProductDto;

public interface ProductWriteUseCase {
    void createProduct(Long memberId, RegisterProductDto command) throws Exception;

    int decreaseStock(Long productId, Long quantity);

    Boolean incrementStock(Long productId, Long quantity);

}
