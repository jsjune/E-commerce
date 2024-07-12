package com.productservice.usecase;

import com.productservice.usecase.dto.ProductDto;
import com.productservice.usecase.dto.ProductListResponseDto;
import com.productservice.usecase.dto.ProductResponseDto;
import org.springframework.data.domain.Pageable;
public interface ProductReadUseCase {

    ProductResponseDto getProduct(Long productId) throws Exception;

    ProductListResponseDto getProducts(String type, String keyword, Pageable pageable);

    ProductDto findProductById(Long productId);
}
