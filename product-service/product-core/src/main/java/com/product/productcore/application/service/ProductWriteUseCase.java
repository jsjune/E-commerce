package com.product.productcore.application.service;


import com.product.productcore.application.service.dto.RegisterProductDto;

public interface ProductWriteUseCase {
    void createProduct(Long memberId, RegisterProductDto command) throws Exception;

}
