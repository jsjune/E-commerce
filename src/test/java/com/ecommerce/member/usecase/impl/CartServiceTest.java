package com.ecommerce.member.usecase.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ecommerce.IntegrationTestSupport;
import com.ecommerce.member.controller.res.CartResponseDto;
import com.ecommerce.member.entity.Cart;
import com.ecommerce.member.entity.Member;
import com.ecommerce.member.repository.CartRepository;
import com.ecommerce.member.repository.MemberRepository;
import com.ecommerce.member.usecase.CartUseCase;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CartServiceTest extends IntegrationTestSupport {

    @Autowired
    private CartUseCase cartUseCase;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CartRepository cartRepository;

    @BeforeEach
    void setUp() {
        cartRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
    }

    @DisplayName("장바구니 비우기")
    @Test
    void clear_cart() {
        // given
        Member member = Member.builder().build();
        memberRepository.save(member);
        Product product = Product.builder().build();
        Product product2 = Product.builder().build();
        productRepository.save(product);
        productRepository.save(product2);
        cartUseCase.addCart(member.getId(), product.getId());
        cartUseCase.addCart(member.getId(), product2.getId());
        cartUseCase.addCart(member.getId(), product2.getId());
        List<Long> productId = List.of(product.getId(), product2.getId());

        // when
        cartRepository.deleteAllByMemberIdAndProductIdIn(member.getId(), productId);
        List<Cart> result = cartRepository.findAll();

        // then
        assertEquals(result.size(), 0);

    }

    @DisplayName("장바구니 수량 조절하기")
    @Test
    void update_cart_quantity() {
        // given
        Member member = Member.builder().build();
        memberRepository.save(member);
        Product product = Product.builder().build();
        productRepository.save(product);
        cartUseCase.addCart(member.getId(), product.getId());
        Cart cart = cartRepository.findAll().stream().findFirst().get();

        // when
        cartUseCase.updateCartQuantity(member.getId(), cart.getId(), 3);
        Cart result = cartRepository.findById(cart.getId()).get();

        // then
        assertEquals(result.getQuantity(), 3);
    }

    @DisplayName("장바구니 목록 조회")
    @Test
    void get_cart_list() {
        // given
        Member member = Member.builder().build();
        memberRepository.save(member);
        Product product = Product.builder().productImages(new ArrayList<>()).build();
        Product product2 = Product.builder().productImages(new ArrayList<>()).build();
        productRepository.save(product);
        productRepository.save(product2);
        cartUseCase.addCart(member.getId(), product.getId());
        cartUseCase.addCart(member.getId(), product2.getId());

        // when
        CartResponseDto cartList = cartUseCase.getCartList(member.getId());

        // then
        assertEquals(cartList.getCarts().size(), 2);
    }

    @DisplayName("장바구니 삭제")
    @Test
    void delete_cart() {
        // given
        Member member = Member.builder().build();
        memberRepository.save(member);
        Product product = Product.builder().build();
        productRepository.save(product);
        cartUseCase.addCart(member.getId(), product.getId());
        Cart cart = cartRepository.findAll().stream().findFirst().get();

        // when
        cartUseCase.deleteCart(member.getId(), cart.getId());

        // then
        assertTrue(cartRepository.findAll().isEmpty());
    }

    @DisplayName("기존 장바구니에 상품이 존재할 때 수량 증가")
    @Test
    void increase_product_quantity() {
        // given
        Member member = Member.builder()
            .username("abc")
            .build();
        memberRepository.save(member);
        Product product = Product.builder()
            .name("iPone")
            .seller(Member.builder().build())
            .build();
        productRepository.save(product);

        // when
        cartUseCase.addCart(member.getId(), product.getId());
        cartUseCase.addCart(member.getId(), product.getId());

        // then
        assertEquals(member.getCarts().size(), 1);
        assertEquals(member.getCarts().get(0).getQuantity(), 2);
        assertEquals(member.getCarts().get(0).getProduct(), product);

    }

    @DisplayName("장바구니 추가")
    @Test
    void add_cart() {
        // given
        Member member = Member.builder()
            .username("abc")
            .build();
        memberRepository.save(member);
        Product product = Product.builder()
            .name("iPone")
            .seller(Member.builder().build())
            .build();
        productRepository.save(product);

        // when
        cartUseCase.addCart(member.getId(), product.getId());

        // then
        assertEquals(member.getCarts().size(), 1);
        assertEquals(member.getCarts().get(0).getProduct(), product);
    }
}
