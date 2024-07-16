package com.member.membercore.application.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.member.memberapi.usecase.CartUseCase;
import com.member.memberapi.usecase.InternalCartUseCase;
import com.member.memberapi.usecase.dto.CartDto;
import com.member.membercore.IntegrationTestSupport;
import com.member.membercore.adapter.ProductClient;
import com.member.membercore.application.service.dto.ProductDto;
import com.member.membercore.infrastructure.entity.Cart;
import com.member.membercore.infrastructure.entity.Member;
import com.member.membercore.infrastructure.repository.CartRepository;
import com.member.membercore.infrastructure.repository.MemberRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class InternalCartServiceTest extends IntegrationTestSupport {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CartUseCase cartUseCase;
    @Autowired
    private CartRepository cartRepository;
    @MockBean
    private ProductClient productClient;
    @Autowired
    private InternalCartUseCase internalCartUseCase;

    @BeforeEach
    void setUp() {
        cartRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("서버간 내부 통신 - 장바구니 비우기")
    @Test
    void clear_cart() {
        // given
        Member member = Member.builder().build();
        memberRepository.save(member);

        ProductDto product1 = registeredProduct(1L);
        ProductDto product2 = registeredProduct(2L);
        when(productClient.getProduct(product1.productId())).thenReturn(product1);
        when(productClient.getProduct(product2.productId())).thenReturn(product2);
        cartUseCase.addCart(member.getId(), product1.productId());
        cartUseCase.addCart(member.getId(), product2.productId());
        cartUseCase.addCart(member.getId(), product2.productId());
        List<Cart> findCarts = cartRepository.findAll();
        List<Long> cartIds = findCarts.stream().map(Cart::getId).toList();

        // when
        internalCartUseCase.clearCart(member.getId(), cartIds);
        List<Cart> result = cartRepository.findAll();

        // then
        assertEquals(result.size(), 0);

    }

    @DisplayName("서버간 내부 통신 - 장바구니 조회")
    @Test
    void test() {
        // given
        Member member = Member.builder().build();
        memberRepository.save(member);
        ProductDto product1 = registeredProduct(1L);
        ProductDto product2 = registeredProduct(2L);
        when(productClient.getProduct(product1.productId())).thenReturn(product1);
        when(productClient.getProduct(product2.productId())).thenReturn(product2);
        cartUseCase.addCart(member.getId(), product1.productId());
        cartUseCase.addCart(member.getId(), product2.productId());
        cartUseCase.addCart(member.getId(), product2.productId());
        List<Long> cartIds = cartRepository.findAll().stream().map(Cart::getId).toList();

        // when
        List<CartDto> result = internalCartUseCase.getCartList(member.getId(), cartIds);

        // then
        assertEquals(result.size(), 2);
    }

    private static ProductDto registeredProduct(Long productId) {
        return new ProductDto(productId, "상품" + productId, 1000L, "썸네일");
    }
}
