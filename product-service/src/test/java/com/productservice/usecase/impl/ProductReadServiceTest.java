package com.productservice.usecase.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.productservice.IntegrationTestSupport;
import com.productservice.adapter.MemberClient;
import com.productservice.usecase.dto.MemberDto;
import com.productservice.usecase.dto.ProductListResponseDto;
import com.productservice.usecase.dto.ProductResponseDto;
import com.productservice.entity.Product;
import com.productservice.entity.ProductImage;
import com.productservice.entity.Seller;
import com.productservice.repository.ProductRepository;
import com.productservice.usecase.ProductReadUseCase;
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

    @BeforeEach
    void setUp() {
        productRepository.deleteAllInBatch();
    }

    @DisplayName("상품 목록 조회")
    @Test
    void get_products() throws Exception {
        // given
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
        ProductListResponseDto response = productReadUseCase.getProducts(pageable);

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

        // when
        ProductResponseDto response = productReadUseCase.getProduct(product.getId());

        // then
        assertEquals(response.name(), product.getName());
        assertEquals(response.phoneNumber(), member.phoneNumber());

    }

    private MemberDto registerMember(String phoneNumber) {
        return new MemberDto(1L, phoneNumber, "samsung");
    }
}
