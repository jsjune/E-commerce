package com.ecommerce.product.usecase;

import com.ecommerce.product.controller.res.ProductListResponse;
import com.ecommerce.product.controller.res.ProductResponse;
import org.springframework.data.domain.Pageable;
public interface ProductReadUseCase {

    ProductResponse getProduct(Long id);

    ProductListResponse getProducts(Pageable pageable);
}
