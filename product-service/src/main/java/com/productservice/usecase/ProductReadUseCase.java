package com.productservice.usecase;

import com.productservice.controller.internal.res.ProductDto;
import com.productservice.controller.res.ProductListResponseDto;
import com.productservice.controller.res.ProductResponseDto;
import org.springframework.data.domain.Pageable;
public interface ProductReadUseCase {

    ProductResponseDto getProduct(Long productId) throws Exception;

    ProductListResponseDto getProducts(Pageable pageable);

    ProductDto findProductById(Long productId);
}
