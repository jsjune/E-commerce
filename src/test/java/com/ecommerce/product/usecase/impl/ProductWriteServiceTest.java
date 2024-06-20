package com.ecommerce.product.usecase.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ecommerce.IntegrationTestSupport;
import com.ecommerce.common.S3Utils;
import com.ecommerce.common.error.ErrorCode;
import com.ecommerce.common.error.GlobalException;
import com.ecommerce.member.auth.LoginUser;
import com.ecommerce.member.entity.Member;
import com.ecommerce.member.entity.UserRole;
import com.ecommerce.member.repository.MemberRepository;
import com.ecommerce.product.controller.req.ProductRequest;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.usecase.ProductWriteUseCase;
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
    @Autowired
    private MemberRepository memberRepository;
    @MockBean
    private S3Utils s3Utils;

    @BeforeEach
    void setUp() {
        productRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("이미지 허용되지 않는 포멧 시 에러 발생")
    @Test
    void invalid_file_format() {
        // given
        Member member = registerMember();
        String originalFilename = "image";
        ProductRequest request = ProductRequest.builder()
            .name("abc")
            .description("상품 설명")
            .price(10000)
            .stock(100)
            .company("회사명")
            .tags(Set.of("태그1", "태그2"))
            .productImages(List.of(
                new MockMultipartFile("image", originalFilename, "image/jpeg", "image".getBytes())))
            .build();

        // when then
        GlobalException exception = assertThrows(GlobalException.class,
            () -> productWriteUseCase.createProduct(new LoginUser(member), request));
        assertEquals(exception.getErrorCode(), ErrorCode.INVALID_FILE_FORMAT);

    }

    @DisplayName("이미지 허용되지 않는 MIME 타입 시 에러 발생")
    @Test
    void unsupported_MIME_type() {
        // given
        Member member = registerMember();
        String contentType = "text/html";
        ProductRequest request = ProductRequest.builder()
            .name("abc")
            .description("상품 설명")
            .price(10000)
            .stock(100)
            .company("회사명")
            .tags(Set.of("태그1", "태그2"))
            .productImages(List.of(
                new MockMultipartFile("image", "image.jpg", contentType, "image".getBytes())))
            .build();

        // when then
        GlobalException exception = assertThrows(GlobalException.class,
            () -> productWriteUseCase.createProduct(new LoginUser(member), request));
        assertEquals(exception.getErrorCode(), ErrorCode.UNSUPPORTED_MIME_TYPE);

    }

    @DisplayName("이미지 허용되지 않는 확장자 시 에러 발생")
    @Test
    void invalid_file_extension() {
        // given
        Member member = registerMember();
        String originalFilename = "image.svg";
        ProductRequest request = ProductRequest.builder()
            .name("abc")
            .description("상품 설명")
            .price(10000)
            .stock(100)
            .company("회사명")
            .tags(Set.of("태그1", "태그2"))
            .productImages(List.of(
                new MockMultipartFile("image", originalFilename, "image/jpeg", "image".getBytes())))
            .build();

        // when then
        GlobalException exception = assertThrows(GlobalException.class,
            () -> productWriteUseCase.createProduct(new LoginUser(member), request));
        assertEquals(exception.getErrorCode(), ErrorCode.INVALID_FILE_EXTENSION);
    }

    @DisplayName("상품 등록하기")
    @Test
    void createProduct() {
        // given
        Member member = registerMember();
        ProductRequest request = ProductRequest.builder()
            .name("abc")
            .description("상품 설명")
            .price(10000)
            .stock(100)
            .company("회사명")
            .tags(Set.of("태그1", "태그2"))
            .productImages(List.of(
                new MockMultipartFile("image", "image.jpg", "image/jpeg", "image".getBytes())))
            .build();

        // when
        when(s3Utils.uploadThumbFile(any(), any())).thenReturn("imageUrl");
        when(s3Utils.uploadFile(any(), any())).thenReturn("s_imageUrl");
        productWriteUseCase.createProduct(new LoginUser(member), request);

        // then
        productRepository.findAll().stream().findFirst().ifPresent(product -> {
            assertEquals(product.getName(), request.getName());
            assertEquals(product.getTags().size(), request.getTags().size());
            assertEquals(product.getProductImages().size(), 1);
        });

    }

    private Member registerMember() {
        Member member = Member.builder()
            .username("가나다")
            .role(UserRole.USER)
            .build();
        memberRepository.save(member);
        return member;
    }

}
