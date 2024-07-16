package com.product.productapi.usecase;

import com.product.productapi.usecase.dto.ProductListResponseDto;
import com.product.productapi.usecase.dto.ProductResponseDto;
import org.springframework.data.domain.Pageable;

public interface ProductReadUseCase {

    ProductResponseDto getProduct(Long productId) throws Exception;

    ProductListResponseDto getProducts(String type, String keyword, Pageable pageable);
}
