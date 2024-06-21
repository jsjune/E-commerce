package com.ecommerce.product.usecase;

import com.ecommerce.product.controller.res.ProductListResponseDto;
import com.ecommerce.product.controller.res.ProductResponseDto;
import com.ecommerce.product.entity.Product;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
public interface ProductReadUseCase {

    ProductResponseDto getProduct(Long id);

    ProductListResponseDto getProducts(Pageable pageable);

    Optional<Product> findById(Long productId);
}
