package com.product.productcore.application.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.product.productcore.application.service.ProductDecreaseUseCase;
import com.product.productcore.infrastructure.entity.Product;
import com.product.productcore.infrastructure.repository.ProductRepository;
import com.product.productcore.testConfig.IntegrationTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ProductDecreaseServiceTest extends IntegrationTestSupport {

    @Autowired
    private ProductDecreaseUseCase productDecreaseUseCase;
    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAllInBatch();
    }

    @DisplayName("주문시 재고가 없을때")
    @Test
    void none_stock() {
        // given
        Product product = Product.builder().totalStock(1L).soldQuantity(99L).build();
        productRepository.save(product);

        // when
        int status = productDecreaseUseCase.decreaseStock(product.getId(), 10L);
        Product result = productRepository.findById(product.getId()).get();

        // then
        assertEquals(status, -1);
        assertEquals(result.getTotalStock(), 1);
    }

    @DisplayName("주문시 재고 감소")
    @Test
    void decrease_stock() {
        // given
        Product product = Product.builder().totalStock(100L).soldQuantity(0L).build();
        productRepository.save(product);

        // when
        int status = productDecreaseUseCase.decreaseStock(product.getId(), 10L);
        Product result = productRepository.findById(product.getId()).get();

        // then
        assertEquals(status, 1);
        assertEquals(result.getTotalStock(), 90);
        assertEquals(result.getSoldQuantity(), 10);
    }
}
