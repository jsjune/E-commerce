package com.memberservice.usecase.impl;


import com.memberservice.adapter.ProductClient;
import com.memberservice.controller.internal.res.CartDto;
import com.memberservice.adapter.dto.ProductDto;
import com.memberservice.controller.res.CartListDto;
import com.memberservice.controller.res.CartResponseDto;
import com.memberservice.entity.Cart;
import com.memberservice.entity.Member;
import com.memberservice.repository.CartRepository;
import com.memberservice.repository.MemberRepository;
import com.memberservice.usecase.CartUseCase;
import com.memberservice.utils.error.ErrorCode;
import com.memberservice.utils.error.GlobalException;
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
    private final ProductClient productClient;

    @Override
    public void addCart(Long memberId, Long productId) {
        memberRepository.findById(memberId).ifPresent(member -> {
            ProductDto product = productClient.getProduct(productId);
            if (product == null) {
                throw new GlobalException(ErrorCode.PRODUCT_NOT_FOUND);
            }
            for (Cart cart : member.getCarts()) {
                if (cart.getProductId().equals(productId)) {
                    cart.increaseQuantity(1);
                    return;
                }
            }
            Cart cart = Cart.builder()
                .member(member)
                .productId(product.productId())
                .productName(product.productName())
                .price(product.price())
                .thumbnailUrl(product.thumbnailUrl())
                .quantity(1)
                .build();
            member.addCart(cart);
            cartRepository.save(cart);
            memberRepository.save(member);
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
                    cart.getProductId(),
                    cart.getProductName(),
                    cart.getPrice(),
                    cart.getQuantity(),
                    cart.getThumbnailUrl()
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

    @Override
    public List<CartDto> getCartList(Long memberId, List<Long> cartIds) {
        return cartRepository.findAllByIdInAndMemberId(cartIds, memberId).stream()
            .map(CartDto::new).toList();
    }
}
