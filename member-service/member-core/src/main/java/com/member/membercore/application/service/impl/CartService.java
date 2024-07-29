package com.member.membercore.application.service.impl;


import com.ecommerce.common.cache.CachingCartListDto;
import com.member.membercore.adapter.ProductClient;
import com.member.membercore.application.service.CartUseCase;
import com.member.membercore.application.service.dto.CartResponseDto;
import com.member.membercore.application.service.dto.ProductDto;
import com.member.membercore.config.common.error.ErrorCode;
import com.member.membercore.config.common.error.GlobalException;
import com.member.membercore.infrastructure.entity.Cart;
import com.member.membercore.infrastructure.repository.CartRepository;
import com.member.membercore.infrastructure.repository.MemberRepository;
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
        List<CachingCartListDto> cartList = cartCacheService.getCartList(memberId);
        return new CartResponseDto(cartList);
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
}
