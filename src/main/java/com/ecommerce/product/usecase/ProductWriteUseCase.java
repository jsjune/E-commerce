package com.ecommerce.product.usecase;

import com.ecommerce.member.auth.LoginUser;
import com.ecommerce.product.controller.req.ProductRequest;

public interface ProductWriteUseCase {
    void createProduct(LoginUser loginUser, ProductRequest request);

}
