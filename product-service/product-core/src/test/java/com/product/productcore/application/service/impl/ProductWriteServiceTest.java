package com.product.productcore.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.product.productapi.usecase.ProductWriteUseCase;
import com.product.productapi.usecase.dto.RegisterProductDto;
import com.product.productcore.openfeign.MemberClient;
import com.product.productcore.application.service.dto.ImageUploadEvent;
import com.product.productcore.application.service.dto.MemberDto;
import com.product.productcore.infrastructure.repository.ProductRepository;
import com.product.productcore.testConfig.IntegrationTestSupport;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@RecordApplicationEvents
class ProductWriteServiceTest extends IntegrationTestSupport {

    @Autowired
    private ProductWriteUseCase productWriteUseCase;
    @Autowired
    private ProductRepository productRepository;
    @MockBean
    private MemberClient memberClient;
    @Autowired
    private ApplicationEvents events;

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
        productWriteUseCase.createProduct(member.memberId(), command);

        // then
        long count = events.stream(ImageUploadEvent.class).count();
        assertEquals(count, 1);
        productRepository.findAll().stream().findFirst().ifPresent(product -> {
            assertEquals(product.getSeller().getCompany(), member.company());
            assertEquals(product.getName(), command.name());
            assertEquals(product.getTags().size(), command.tags().size());
        });

    }

    private MemberDto registerMember() {
        return new MemberDto(1L, "seller", "samsung");
    }

}
