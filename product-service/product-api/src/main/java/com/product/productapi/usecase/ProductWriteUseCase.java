package com.product.productapi.usecase;


import com.product.productapi.usecase.dto.RegisterProductDto;

public interface ProductWriteUseCase {
    void createProduct(Long memberId, RegisterProductDto command) throws Exception;

}
