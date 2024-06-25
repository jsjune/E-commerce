package com.productservice.usecase;

import com.productservice.usecase.dto.RegisterProductDto;

public interface ProductWriteUseCase {
    void createProduct(Long memberId, RegisterProductDto command) throws Exception;

    Boolean decreaseStock(Long productId, int quantity);

    Boolean incrementStock(Long productId, int quantity);

}
