package com.productservice.usecase;

import com.productservice.adapter.dto.ProductDto;
import com.productservice.controller.res.ProductListResponseDto;
import com.productservice.controller.res.ProductResponseDto;
import com.productservice.entity.Product;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
public interface ProductReadUseCase {

    ProductResponseDto getProduct(Long productId);

    ProductListResponseDto getProducts(Pageable pageable);

    ProductDto findProductById(Long productId);
}
