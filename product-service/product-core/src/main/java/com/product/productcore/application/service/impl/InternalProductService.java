package com.product.productcore.application.service.impl;

import com.product.productcore.application.service.InternalProductUseCase;
import com.product.productcore.application.service.dto.ProductDto;
import com.product.productcore.infrastructure.entity.Product;
import com.product.productcore.infrastructure.entity.ProductImage;
import com.product.productcore.infrastructure.repository.ProductRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class InternalProductService implements InternalProductUseCase {

    private final ProductRepository productRepository;

    @Override
    @Cacheable(cacheNames = "product", key = "#productId")
    public ProductDto findProductById(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        return ProductDto.builder()
            .productId(product.getId())
            .productName(product.getName())
            .price(product.getPrice())
            .thumbnailUrl(product.getProductImages().stream()
                .map(ProductImage::getThumbnailUrl)
                .findFirst().orElse(null))
            .build();
    }

    @Override
    public Boolean incrementStock(Long productId, Long quantity) {
        Optional<Product> findProduct = productRepository.findById(productId);
        if (findProduct.isPresent()) {
            Product product = findProduct.get();
            product.incrementStock(quantity);
            return true;
        }
        return null;
    }
}
