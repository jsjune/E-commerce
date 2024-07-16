package com.member.membercore.application.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.ecommerce.common.cache.CachingCartListDto;
import com.member.memberapi.usecase.CartUseCase;
import com.member.membercore.IntegrationTestSupport;
import com.member.membercore.adapter.ProductClient;
import com.member.membercore.application.service.dto.ProductDto;
import com.member.membercore.infrastructure.entity.Member;
import com.member.membercore.infrastructure.repository.CartRepository;
import com.member.membercore.infrastructure.repository.MemberRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class CartCacheServiceTest extends IntegrationTestSupport {

    @Autowired
    private CartCacheService cartCacheService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartUseCase cartUseCase;
    @MockBean
    private ProductClient productClient;

    @BeforeEach
    void setUp() {
        cartRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("장바구니 목록 조회")
    @Test
    void get_cart_list() {
        // given
        Member member = Member.builder().build();
        memberRepository.save(member);
        long productId_1 = 1L;
        long productId_2 = 2L;
        ProductDto product_1 = ProductDto.builder()
            .productId(productId_1)
            .build();
        ProductDto product_2 = ProductDto.builder()
            .productId(productId_2)
            .build();
        when(productClient.getProduct(productId_1)).thenReturn(product_1);
        when(productClient.getProduct(productId_2)).thenReturn(product_2);
        cartUseCase.addCart(member.getId(), productId_1);
        cartUseCase.addCart(member.getId(), productId_2);

        // when
        List<CachingCartListDto> result = cartCacheService.getCartList(member.getId());

        // then
        assertEquals(result.size(), 2);
    }
}
