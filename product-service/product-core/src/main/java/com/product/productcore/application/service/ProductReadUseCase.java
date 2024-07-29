package com.product.productcore.application.service;

import com.product.productcore.application.service.dto.ProductListResponseDto;
import com.product.productcore.application.service.dto.ProductResponseDto;
import org.springframework.data.domain.Pageable;

public interface ProductReadUseCase {

    ProductResponseDto getProduct(Long productId) throws Exception;

    ProductListResponseDto getProducts(String type, String keyword, Pageable pageable);
}
