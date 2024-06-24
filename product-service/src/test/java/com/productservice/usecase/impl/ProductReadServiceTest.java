package com.productservice.usecase.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.productservice.IntegrationTestSupport;
import com.productservice.adapter.MemberClient;
import com.productservice.adapter.dto.MemberDto;
import com.productservice.controller.res.ProductListResponseDto;
import com.productservice.controller.res.ProductResponseDto;
import com.productservice.entity.Product;
import com.productservice.entity.ProductImage;
import com.productservice.repository.ProductRepository;
import com.productservice.usecase.ProductReadUseCase;
import com.productservice.utils.error.ErrorCode;
import com.productservice.utils.error.GlobalException;
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
        MemberDto member = registerMember();
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Product product = Product.builder()
                .name("abc")
                .sellerId(member.memberId())
                .company(member.phoneNumber())
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
    void get_product() throws Exception {
        // given
        MemberDto member = registerMember();
        Product product = Product.builder()
            .name("가나다")
            .sellerId(member.memberId())
            .phoneNumber(member.phoneNumber())
            .productImages(
                List.of(new ProductImage("image", "imagePath", "thumbnail", "thumbnailPath")))
            .build();
        productRepository.save(product);

        // when
        when(memberClient.getMemberInfo(any())).thenReturn(member);
        ProductResponseDto response = productReadUseCase.getProduct(product.getId());

        // then
        assertEquals(response.getName(), product.getName());
        assertEquals(response.getPhoneNumber(), member.phoneNumber());
    }

    private MemberDto registerMember() {
        return new MemberDto(1L, "seller", "samsung");
    }
}
