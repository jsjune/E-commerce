package com.ecommerce.product.usecase;

import com.ecommerce.member.auth.LoginUser;
import com.ecommerce.product.controller.req.ProductRequestDto;

public interface ProductWriteUseCase {
    void createProduct(Long memberId, ProductRequestDto request);

    void decreaseStock(Long productId, int quantity);

    void incrementStock(Long productId, int quantity);

}
