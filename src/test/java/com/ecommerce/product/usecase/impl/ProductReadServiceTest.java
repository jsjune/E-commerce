package com.ecommerce.product.usecase.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.ecommerce.IntegrationTestSupport;
import com.ecommerce.common.error.ErrorCode;
import com.ecommerce.common.error.GlobalException;
import com.ecommerce.member.entity.Member;
import com.ecommerce.member.entity.UserRole;
import com.ecommerce.member.repository.MemberRepository;
import com.ecommerce.product.controller.res.ProductListResponseDto;
import com.ecommerce.product.controller.res.ProductResponseDto;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.entity.ProductImage;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.usecase.ProductReadUseCase;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class ProductReadServiceTest extends IntegrationTestSupport {

    @Autowired
    private ProductReadUseCase productReadUseCase;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("상품 목록 조회")
    @Test
    void get_products() {
        // given
        Member member = Member.builder()
            .role(UserRole.SELLER)
            .build();
        memberRepository.save(member);
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Product product = Product.builder()
                .name("abc")
                .seller(member)
                .productImages(
                    List.of(new ProductImage("image", "imagePath", "thumbnail", "thumbnailPath")))
                .build();
            products.add(product);
        }
        productRepository.saveAll(products);
        Pageable pageable = PageRequest.of(0, 10);

        // when
        ProductListResponseDto response = productReadUseCase.getProducts(pageable);

        // then
        assertEquals(response.getProducts().size(), products.size());

    }

    @DisplayName("상품 조회시 존재하지 않는 경우 에러 발생")
    @Test
    void not_find_product() {
        // given
        Long id = 1L;

        // when then
        GlobalException exception = assertThrows(GlobalException.class,
            () -> productReadUseCase.getProduct(id));
        assertEquals(exception.getErrorCode(), ErrorCode.PRODUCT_NOT_FOUND);
    }

    @DisplayName("상품 상세 조회")
    @Test
    void get_product() {
        // given
        Member member = Member.builder()
            .role(UserRole.SELLER)
            .build();
        memberRepository.save(member);
        Product product = Product.builder()
            .name("가나다")
            .seller(member)
            .productImages(
                List.of(new ProductImage("image", "imagePath", "thumbnail", "thumbnailPath")))
            .build();
        productRepository.save(product);

        // when
        ProductResponseDto response = productReadUseCase.getProduct(product.getId());

        // then
        assertEquals(response.getName(), product.getName());
    }
}
