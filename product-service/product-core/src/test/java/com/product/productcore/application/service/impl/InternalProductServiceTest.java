package com.product.productcore.application.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.product.productapi.usecase.InternalProductUseCase;
import com.product.productapi.usecase.dto.ProductDto;
import com.product.productcore.infrastructure.entity.Product;
import com.product.productcore.infrastructure.entity.ProductImage;
import com.product.productcore.infrastructure.repository.ProductRepository;
import com.product.productcore.testConfig.IntegrationTestSupport;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class InternalProductServiceTest extends IntegrationTestSupport {

    @Autowired
    private InternalProductUseCase internalProductUseCase;
    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAllInBatch();
    }

    @DisplayName("상품 조회")
    @Test
    void find_product_by_id() {
        // given
        String name = "abc";
        long price = 1000L;
        String thumbnailUrl = "image.url";
        Product product = Product.builder()
            .name(name)
            .price(price)
            .productImages(List.of(ProductImage.builder().thumbnailUrl(thumbnailUrl).build()))
            .build();
        productRepository.save(product);

        // when
        ProductDto result = internalProductUseCase.findProductById(product.getId());

        // then
        assertEquals(result.productName(), name);
        assertEquals(result.price(), price);
        assertEquals(result.thumbnailUrl(), thumbnailUrl);
    }

    @DisplayName("주문 취소시 재고 증가")
    @Test
    void increment_stock() {
        // given
        Product product = Product.builder().totalStock(90L).soldQuantity(10L).build();
        productRepository.save(product);

        // when
        internalProductUseCase.incrementStock(product.getId(), 10L);
        Product result = productRepository.findById(product.getId()).get();

        // then
        assertEquals(result.getTotalStock(), 100);
        assertEquals(result.getSoldQuantity(), 0);

    }

    @DisplayName("주문시 재고가 없을때")
    @Test
    void none_stock() {
        // given
        Product product = Product.builder().totalStock(1L).soldQuantity(99L).build();
        productRepository.save(product);

        // when
        int status = internalProductUseCase.decreaseStock(product.getId(), 10L);
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
        internalProductUseCase.decreaseStock(product.getId(), 10L);
        Product result = productRepository.findById(product.getId()).get();

        // then
        assertEquals(result.getTotalStock(), 90);
        assertEquals(result.getSoldQuantity(), 10);
    }
}
