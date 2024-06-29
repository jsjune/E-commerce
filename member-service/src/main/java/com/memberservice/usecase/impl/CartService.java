package com.memberservice.usecase.impl;


import com.memberservice.adapter.ProductClient;
import com.memberservice.adapter.dto.ProductDto;
import com.memberservice.controller.internal.res.CartDto;
import com.memberservice.controller.res.CartResponseDto;
import com.memberservice.entity.Cart;
import com.memberservice.repository.CartRepository;
import com.memberservice.repository.MemberRepository;
import com.memberservice.usecase.CartUseCase;
import com.memberservice.utils.error.ErrorCode;
import com.memberservice.utils.error.GlobalException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService implements CartUseCase {

    private final CartRepository cartRepository;
    private final MemberRepository memberRepository;
    private final ProductClient productClient;
    private final CartCacheService cartCacheService;

    @Override
    @CacheEvict(value = "cartList", key = "#memberId")
    public void addCart(Long memberId, Long productId) {
        memberRepository.findById(memberId).ifPresent(member -> {
            ProductDto product = productClient.getProduct(productId);
            if (product == null) {
                throw new GlobalException(ErrorCode.PRODUCT_NOT_FOUND);
            }
            for (Cart cart : member.getCarts()) {
                if (cart.getProductId().equals(productId)) {
                    cart.increaseQuantity(1L);
                    return;
                }
            }
            Cart cart = Cart.builder()
                .member(member)
                .productId(product.productId())
                .productName(product.productName())
                .price(product.price())
                .thumbnailUrl(product.thumbnailUrl())
                .quantity(1L)
                .build();
            member.addCart(cart);
            cartRepository.save(cart);
            memberRepository.save(member);
        });
    }

    @Override
    @CacheEvict(value = "cartList", key = "#memberId")
    public void deleteCart(Long memberId, Long cartId) {
        cartRepository.deleteByIdAndMemberId(cartId, memberId);
    }

    @Override
    public CartResponseDto getCartList(Long memberId) {
        return new CartResponseDto(cartCacheService.getCartList(memberId));
    }

    @Override
    @CacheEvict(value = "cartList", key = "#memberId")
    public void updateCartQuantity(Long memberId, Long cartId, Long quantity) {
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
    @CacheEvict(value = "cartList", key = "#memberId")
    public void clearCart(Long memberId, List<Long> productId) {
        cartRepository.deleteAllByMemberIdAndProductIdIn(memberId, productId);
    }

    @Override
    public List<CartDto> getCartList(Long memberId, List<Long> cartIds) {
        return cartCacheService.getCartList(memberId).stream()
            .filter(cartListDto -> cartIds.contains(cartListDto.cartId()))
            .map(CartDto::new)
            .toList();
    }
}
