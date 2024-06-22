package com.ecommerce.member.usecase.impl;

import com.ecommerce.member.controller.res.CartListDto;
import com.ecommerce.member.controller.res.CartResponseDto;
import com.ecommerce.member.entity.Cart;
import com.ecommerce.member.entity.Member;
import com.ecommerce.member.repository.CartRepository;
import com.ecommerce.member.repository.MemberRepository;
import com.ecommerce.member.usecase.CartUseCase;
import com.ecommerce.product.entity.ProductImage;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.usecase.ProductReadUseCase;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService implements CartUseCase {

    private final CartRepository cartRepository;
    private final MemberRepository memberRepository;
    private final ProductReadUseCase productReadUseCase;

    @Override
    public void addCart(Long memberId, Long productId) {
        memberRepository.findById(memberId).ifPresent(member -> {
            productReadUseCase.findById(productId).ifPresent(product -> {
                for (Cart cart : member.getCarts()) {
                    if (cart.getProduct().getId().equals(productId)) {
                        cart.increaseQuantity(1);
                        return;
                    }
                }
                Cart cart = new Cart(1, product, member);
                member.addCart(cart);
                cartRepository.save(cart);
                memberRepository.save(member);
            });
        });
    }

    @Override
    public void deleteCart(Long memberId, Long cartId) {
        cartRepository.deleteByIdAndMemberId(cartId, memberId);
    }

    @Override
    public CartResponseDto getCartList(Long memberId) {
        Optional<Member> findMember = memberRepository.findById(memberId);
        if (findMember.isPresent()) {
            Member member = findMember.get();
            List<CartListDto> cartList = member.getCarts().stream()
                .map(cart -> new CartListDto(
                    cart.getId(),
                    cart.getProduct().getId(),
                    cart.getProduct().getName(),
                    cart.getProduct().getPrice(),
                    cart.getQuantity(),
                    cart.getProduct().getProductImages().stream()
                        .map(ProductImage::getThumbnailUrl)
                        .findFirst().orElse(null)
                )).toList();
            return new CartResponseDto(cartList);
        }
        return null;
    }

    @Override
    public void updateCartQuantity(Long memberId, Long cartId, int quantity) {
        memberRepository.findById(memberId).ifPresent(member -> {
            member.getCarts().stream()
                .filter(cart -> cart.getId().equals(cartId))
                .findFirst()
                .ifPresent(cart -> {
                    cart.updateQuantity(quantity);
                    cartRepository.save(cart);
                });
        });
    }

    @Override
    public void clearCart(Long memberId, List<Long> productId) {
        cartRepository.deleteAllByMemberIdAndProductIdIn(memberId, productId);
    }
}
