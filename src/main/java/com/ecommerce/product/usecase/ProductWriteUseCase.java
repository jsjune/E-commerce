package com.ecommerce.product.usecase;

import com.ecommerce.member.auth.LoginUser;
import com.ecommerce.product.controller.req.ProductRequestDto;

public interface ProductWriteUseCase {
    void createProduct(LoginUser loginUser, ProductRequestDto request);

    void decreaseStock(Long productId, int quantity);
}
