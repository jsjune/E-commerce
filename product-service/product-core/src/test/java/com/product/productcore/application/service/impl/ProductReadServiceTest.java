package com.product.productcore.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.product.productapi.usecase.ProductReadUseCase;
import com.product.productapi.usecase.dto.ProductListResponseDto;
import com.product.productapi.usecase.dto.ProductResponseDto;
import com.product.productcore.application.utils.RedisUtils;
import com.product.productcore.openfeign.MemberClient;
import com.product.productcore.application.service.dto.MemberDto;
import com.product.productcore.infrastructure.entity.Product;
import com.product.productcore.infrastructure.entity.ProductImage;
import com.product.productcore.infrastructure.entity.Seller;
import com.product.productcore.infrastructure.repository.ProductRepository;
import com.product.productcore.testConfig.IntegrationTestSupport;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class ProductReadServiceTest extends IntegrationTestSupport {

    @Autowired
    private ProductReadUseCase productReadUseCase;

    @MockBean
    private MemberClient memberClient;

    @Autowired
    private ProductRepository productRepository;

    @MockBean
    private RedisUtils redisUtils;

    @BeforeEach
    void setUp() {
        productRepository.deleteAllInBatch();
    }

    @DisplayName("상품 검색 조회")
    @Test
    void get_products() throws Exception {
        // given
        String type = "name";
        String keyword = "ab";
        MemberDto member = registerMember("331672794abf3e48bea635a008d36aec");
        Seller seller = new Seller(member.memberId(), member.phoneNumber(), null);
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Product product = Product.builder()
                .name("abc")
                .seller(seller)
                .productImages(
                    List.of(new ProductImage("image", "imagePath", "thumbnail", "thumbnailPath")))
                .build();
            products.add(product);
        }
        productRepository.saveAll(products);
        Pageable pageable = PageRequest.of(0, 10);

        // when
        when(memberClient.getMemberInfo(any())).thenReturn(member);
        ProductListResponseDto response = productReadUseCase.getProducts(type, keyword, pageable);

        // then
        assertEquals(response.products().size(), products.size());
    }

    @DisplayName("상품 조회시 존재하지 않는 경우 null")
    @Test
    void none_find_product() throws Exception {
        // given
        Long id = 1L;

        // when then
        ProductResponseDto product = productReadUseCase.getProduct(id);

        assertNull(product);
    }

    @DisplayName("상품 상세 조회")
    @Test
    void get_product() throws Exception {
        // given
        MemberDto member = registerMember("010-1234-5678");
        Seller seller = new Seller(member.memberId(), "e7c3b80abeb68b5f886523c42c0677a4", null);
        Product product = Product.builder()
            .name("가나다")
            .seller(seller)
            .productImages(
                List.of(new ProductImage("image", "imagePath", "thumbnail", "thumbnailPath")))
            .build();
        productRepository.save(product);
        long totalStock = 10L;
        when(redisUtils.getStock(anyString())).thenReturn(totalStock);

        // when
        ProductResponseDto response = productReadUseCase.getProduct(product.getId());

        // then
        assertEquals(response.name(), product.getName());
        assertEquals(response.phoneNumber(), member.phoneNumber());
        assertEquals(response.totalStock(), totalStock);
    }

    private MemberDto registerMember(String phoneNumber) {
        return new MemberDto(1L, phoneNumber, "samsung");
    }
}
