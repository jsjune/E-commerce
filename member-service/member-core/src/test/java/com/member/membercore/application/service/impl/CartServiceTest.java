package com.member.membercore.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.member.memberapi.usecase.CartUseCase;
import com.member.memberapi.usecase.dto.CartResponseDto;
import com.member.membercore.IntegrationTestSupport;
import com.member.membercore.adapter.ProductClient;
import com.member.membercore.application.service.dto.ProductDto;
import com.member.membercore.infrastructure.entity.Cart;
import com.member.membercore.infrastructure.entity.Member;
import com.member.membercore.infrastructure.repository.CartRepository;
import com.member.membercore.infrastructure.repository.MemberRepository;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class CartServiceTest extends IntegrationTestSupport {

    @Autowired
    private CartUseCase cartUseCase;
    @MockBean
    private ProductClient productClient;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CartRepository cartRepository;

    @BeforeEach
    void setUp() {
        cartRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("장바구니 수량 조절하기")
    @Test
    void update_cart_quantity() {
        // given
        Member member = Member.builder().build();
        memberRepository.save(member);

        ProductDto product = registeredProduct(1L);
        when(productClient.getProduct(product.productId())).thenReturn(product);
        cartUseCase.addCart(member.getId(), product.productId());
        Cart cart = cartRepository.findAll().stream().findFirst().get();

        // when
        cartUseCase.updateCartQuantity(member.getId(), cart.getId(), 3L);
        Cart result = cartRepository.findById(cart.getId()).get();

        // then
        Assertions.assertEquals(result.getQuantity(), 3);
    }

    @DisplayName("장바구니 목록 조회")
    @Test
    void get_cart_list() {
        // given
        Member member = Member.builder().build();
        memberRepository.save(member);
        ProductDto product1 = registeredProduct(1L);
        ProductDto product2 = registeredProduct(2L);

        when(productClient.getProduct(product1.productId())).thenReturn(product1);
        when(productClient.getProduct(product2.productId())).thenReturn(product2);
        cartUseCase.addCart(member.getId(), product1.productId());
        cartUseCase.addCart(member.getId(), product2.productId());

        // when
        CartResponseDto cartList = cartUseCase.getCartList(member.getId());

        // then
        Assertions.assertEquals(cartList.carts().size(), 2);
        Assertions.assertEquals(cartList.carts().get(0).productId(), product1.productId());
        Assertions.assertEquals(cartList.carts().get(1).productId(), product2.productId());
    }

    @DisplayName("장바구니 삭제")
    @Test
    void delete_cart() {
        // given
        Member member = Member.builder().build();
        memberRepository.save(member);
        ProductDto product = registeredProduct(1L);

        when(productClient.getProduct(product.productId())).thenReturn(product);
        cartUseCase.addCart(member.getId(), product.productId());
        Cart cart = cartRepository.findAll().stream().findFirst().get();

        // when
        cartUseCase.deleteCart(member.getId(), cart.getId());

        // then
        Assertions.assertTrue(cartRepository.findAll().isEmpty());
    }

    @DisplayName("기존 장바구니에 상품이 존재할 때 수량 증가")
    @Test
    void increase_product_quantity() {
        // given
        Member member = Member.builder()
            .username("abc")
            .build();
        memberRepository.save(member);
        ProductDto product = registeredProduct(1L);

        // when
        when(productClient.getProduct(product.productId())).thenReturn(product);
        cartUseCase.addCart(member.getId(), product.productId());
        cartUseCase.addCart(member.getId(), product.productId());

        // then
        Assertions.assertEquals(member.getCarts().size(), 1);
        Assertions.assertEquals(member.getCarts().get(0).getQuantity(), 2);
        Assertions.assertEquals(member.getCarts().get(0).getProductName(), product.productName());

    }

    @DisplayName("장바구니 추가")
    @Test
    void add_cart() {
        // given
        Member member = Member.builder()
            .username("abc")
            .build();
        memberRepository.save(member);
        ProductDto product = registeredProduct(1L);

        // when
        when(productClient.getProduct(product.productId())).thenReturn(product);
        cartUseCase.addCart(member.getId(), product.productId());

        // then
        Assertions.assertEquals(member.getCarts().size(), 1);
        Assertions.assertEquals(member.getCarts().get(0).getProductName(), product.productName());
    }

    private static ProductDto registeredProduct(Long productId) {
        return new ProductDto(productId, "상품" + productId, 1000L, "썸네일");
    }
}
