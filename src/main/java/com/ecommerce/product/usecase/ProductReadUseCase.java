package com.ecommerce.product.usecase;

import com.ecommerce.product.controller.res.ProductListResponseDto;
import com.ecommerce.product.controller.res.ProductResponseDto;
import org.springframework.data.domain.Pageable;
public interface ProductReadUseCase {

    ProductResponseDto getProduct(Long id);

    ProductListResponseDto getProducts(Pageable pageable);
}
