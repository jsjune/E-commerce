package com.product.productcore.application.service.impl;

import com.product.productcore.application.service.ProductDecreaseUseCase;
import com.product.productcore.infrastructure.entity.Product;
import com.product.productcore.infrastructure.repository.ProductRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class ProductDecreaseService implements ProductDecreaseUseCase {

    private final ProductRepository productRepository;

    @Override
    public int decreaseStock(Long productId, Long quantity) {
        Optional<Product> findProduct = productRepository.findById(productId);
        if (findProduct.isPresent()) {
            Product product = findProduct.get();
            if (product.getTotalStock() < quantity) {
                return -1;
            }
            product.decreaseStock(quantity);
            return 1;
        }
        return -1;
    }
}
