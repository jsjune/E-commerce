package com.productservice.usecase.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.productservice.testConfig.IntegrationTestSupport;
import com.productservice.adapter.MemberClient;
import com.productservice.usecase.dto.MemberDto;
import com.productservice.entity.Product;
import com.productservice.repository.ProductRepository;
import com.productservice.usecase.ProductWriteUseCase;
import com.productservice.usecase.dto.RegisterProductDto;
import com.productservice.utils.S3Utils;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;


class ProductWriteServiceTest extends IntegrationTestSupport {

    @Autowired
    private ProductWriteUseCase productWriteUseCase;
    @Autowired
    private ProductRepository productRepository;
    @MockBean
    private MemberClient memberClient;
    @MockBean
    private S3Utils s3Utils;

    @BeforeEach
    void setUp() {
        productRepository.deleteAllInBatch();
    }

    @DisplayName("주문 취소시 재고 증가")
    @Test
    void increment_stock() {
        // given
        Product product = Product.builder().totalStock(90L).soldQuantity(10L).build();
        productRepository.save(product);

        // when
        productWriteUseCase.incrementStock(product.getId(), 10L);
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
        int status = productWriteUseCase.decreaseStock(product.getId(), 10L);
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
        productWriteUseCase.decreaseStock(product.getId(), 10L);
        Product result = productRepository.findById(product.getId()).get();

        // then
        assertEquals(result.getTotalStock(), 90);
        assertEquals(result.getSoldQuantity(), 10);
    }

    @DisplayName("상품 등록하기")
    @Test
    void createProduct() throws Exception {
        // given
        MemberDto member = registerMember();
        RegisterProductDto command = RegisterProductDto.builder()
            .name("abc")
            .description("상품 설명")
            .price(10000L)
            .stock(100L)
            .tags(Set.of("태그1", "태그2"))
            .productImages(List.of(
                new MockMultipartFile("image", "image.jpg", "image/jpeg", "image".getBytes())))
            .build();

        // when
        when(memberClient.getMemberInfo(any())).thenReturn(member);
        when(s3Utils.uploadThumbFile(any(), any())).thenReturn("imageUrl");
        when(s3Utils.uploadFile(any(), any())).thenReturn("s_imageUrl");
        productWriteUseCase.createProduct(member.memberId(), command);

        // then
        productRepository.findAll().stream().findFirst().ifPresent(product -> {
            assertEquals(product.getSeller().getCompany(), member.company());
            assertEquals(product.getName(), command.name());
            assertEquals(product.getTags().size(), command.tags().size());
            assertEquals(product.getProductImages().size(), 1);
        });

    }

    private MemberDto registerMember() {
        return new MemberDto(1L, "seller", "samsung");
    }

}
